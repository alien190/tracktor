package com.elegion.tracktor.common;

import android.util.Pair;

import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.LocationJobState;
import com.elegion.tracktor.data.model.RealmLocationData;
import com.elegion.tracktor.service.ITrackHelper;
import com.elegion.tracktor.service.ITrackHelperCallBack;
import com.elegion.tracktor.ui.common.WeatherUpdater;
import com.elegion.tracktor.utils.StringUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;

import static com.elegion.tracktor.utils.CommonUtils.locationDataToRealm;
import static com.elegion.tracktor.utils.CommonUtils.realmToLocationData;

public class KalmanRoute implements ITrackHelper {

    private static String TAG = "KalmanRouteTAG";
    private static double ROUTE_ACCURACY_METERS = 10;
    private double mKoeff;
    private List<LocationData> mRoutePoints = new ArrayList<>();
    private LocationData mLastRawPoint;
    private LocationData mLastPointForSegment;
    private double mDistance;
    private long mTotalSecond;
    private Date mStartDate;
    private double mAverageSpeed;
    private Disposable mTimerDisposable;
    private ITrackHelperCallBack mCallBack;
    private boolean isStarted;
    private WeatherUpdater mWeatherUpdater;


    public KalmanRoute(double koeff) {
        mKoeff = koeff;
    }

    public KalmanRoute(WeatherUpdater weatherUpdater) {
        mKoeff = 1;
        mWeatherUpdater = weatherUpdater;
    }

    private void init() {
        mDistance = 0;
        mTotalSecond = 0;
        mAverageSpeed = 0;
    }

    public boolean loadFromRealm(IRepository repository) {
        LocationJobState state = null;
        if (repository != null) {
            state = repository.getLocationJobState();
            if (state != null) {
                mKoeff = state.getKoeff();
                mRoutePoints = new ArrayList<>();
                for (RealmLocationData data : state.getRoutePoints()) {
                    mRoutePoints.add(realmToLocationData(data));
                }
                mLastRawPoint = realmToLocationData(state.getLastRawPoint());
                mLastPointForSegment = realmToLocationData(state.getLastPointForSegment());
                mDistance = state.getDistance();
                mTotalSecond = state.getTotalSecond();
                mStartDate = state.getStartDate();
                mAverageSpeed = state.getAverageSpeed();
                isStarted = state.isStarted();
                return true;
            }
        }
        fixStartTime();
        return false;
    }

    public void saveToRealm(IRepository repository) {
        if (repository != null) {
            LocationJobState state = new LocationJobState();
            state.setKoeff(mKoeff);
            RealmList<RealmLocationData> routePoints = new RealmList<>();
            for (LocationData data : mRoutePoints) {
                routePoints.add(locationDataToRealm(data));
            }
            state.setRoutePoints(routePoints);
            state.setLastRawPoint(locationDataToRealm(mLastRawPoint));
            state.setLastPointForSegment(locationDataToRealm(mLastPointForSegment));
            state.setDistance(mDistance);
            state.setTotalSecond(mTotalSecond);
            state.setStartDate(mStartDate);
            state.setAverageSpeed(mAverageSpeed);
            state.setStarted(isStarted);
            state.setTemperature(mWeatherUpdater.getTemperature());
            state.setWeatherIcon(mWeatherUpdater.getWeatherIcon());
            state.setWeatherDescription(mWeatherUpdater.getWeatherDescription());
            repository.updateLocationJobState(state);
        }
    }


    @Override
    public void start() {
        fixStartTime();
        mTimerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(seconds -> updateMetrics());
    }

    @Override
    public void resume() {
        mTimerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(seconds -> updateMetrics());
    }

    private void fixStartTime() {
        init();
        isStarted = true;
        mStartDate = Calendar.getInstance().getTime();
    }

    @Override
    public void stop() {
        mTimerDisposable.dispose();
        isStarted = false;
    }

    public void onRouteUpdate(LatLng newPoint) {
        if (isStarted) {
            updateTotalSeconds();

            mLastRawPoint = new LocationData(newPoint, mTotalSecond);
            if (mRoutePoints.size() == 0) {
                mRoutePoints.add(mLastRawPoint);
                mWeatherUpdater.updateWeatherPeriodically(mLastRawPoint);
                if (mCallBack != null) {
                    mCallBack.onFirstPoint(mLastRawPoint);
                }
            } else {
                LocationData lastPoint = mRoutePoints.get(mRoutePoints.size() - 1);
                double newPointLat = mKoeff * mLastRawPoint.point.latitude
                        + (1 - mKoeff) * lastPoint.point.latitude;
                double newPointLng = mKoeff * mLastRawPoint.point.longitude
                        + (1 - mKoeff) * lastPoint.point.longitude;
                LocationData newLocationData = new LocationData(new LatLng(newPointLat, newPointLng),
                        mTotalSecond);
                mRoutePoints.add(newLocationData);
                mWeatherUpdater.updateWeatherPeriodically(newLocationData);
                SegmentForRouteEvent newSegment = getLastSegment();
                if (newSegment != null) {
                    mCallBack.onRouteUpdate(newSegment);
                    mDistance += newSegment.getSegmentDistance();
                }
            }
            updateMetrics();
        }
    }

    private void updateMetrics() {
        updateTotalSeconds();
        mWeatherUpdater.updateWeather();
        mAverageSpeed = mDistance / (mTotalSecond == 0 ? 1 : mTotalSecond);
        if (mCallBack != null) {
            mCallBack.onMetricsUpdate();
        }
    }

    private void updateTotalSeconds() {
        mTotalSecond = (Calendar.getInstance().getTimeInMillis() - mStartDate.getTime()) / 1000;
    }

    @Override
    public String toString() {
        return StringUtils.getLocationDataText(mRoutePoints);
    }

    public SegmentForRouteEvent getLastSegment() {
        int size = mRoutePoints.size();
        if (size > 1) {
            if (mLastPointForSegment == null) {
                mLastPointForSegment = mRoutePoints.get(size - 2);
            }

            if (SphericalUtil.computeDistanceBetween(mLastPointForSegment.point,
                    mLastRawPoint.point) > ROUTE_ACCURACY_METERS) {
                SegmentForRouteEvent segmentForRouteEvent = new SegmentForRouteEvent(
                        new Pair<>(mLastPointForSegment, mRoutePoints.get(size - 1)));
                mLastPointForSegment = mRoutePoints.get(size - 1);
                return segmentForRouteEvent;
            }
        }
        return null;
    }

    public List<LatLng> getRoute() {
        List<LatLng> route = new ArrayList<>();

        for (LocationData locationData : mRoutePoints) {
            route.add(locationData.point);
        }

        return route;
    }

    @Override
    public void setCallBack(ITrackHelperCallBack callBack) {
        mCallBack = callBack;
    }

    public double getDistance() {
        return mDistance;
    }

    public long getTotalSecond() {
        return mTotalSecond;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public double getAverageSpeed() {
        return mAverageSpeed;
    }

    @Override
    public Double getTemperature() {
        return mWeatherUpdater.getTemperature();
    }

    @Override
    public String getWeatherDescription() {
        return mWeatherUpdater.getWeatherDescription();
    }

    @Override
    public String getWeatherIcon() {
        return mWeatherUpdater.getWeatherIcon();
    }

}
