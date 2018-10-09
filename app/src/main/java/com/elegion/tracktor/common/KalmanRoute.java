package com.elegion.tracktor.common;

import android.util.Pair;

import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.service.ITrackHelper;
import com.elegion.tracktor.service.ITrackHelperCallBack;
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


    public KalmanRoute(double koeff) {
        mKoeff = koeff;
    }

    public KalmanRoute() {
        mKoeff = 1;
    }

    private void init() {
        mDistance = 0;
        mTotalSecond = 0;
        mAverageSpeed = 0;
    }

    @Override
    public void start() {
        init();
        isStarted = true;
        mStartDate = Calendar.getInstance().getTime();
        mTimerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(seconds -> updateMetrics());
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
                if (mCallBack != null) {
                    mCallBack.onFirstPoint(mLastRawPoint);
                }
            } else {
                LocationData lastPoint = mRoutePoints.get(mRoutePoints.size() - 1);
                double newPointLat = mKoeff * mLastRawPoint.point.latitude
                        + (1 - mKoeff) * lastPoint.point.latitude;
                double newPointLng = mKoeff * mLastRawPoint.point.longitude
                        + (1 - mKoeff) * lastPoint.point.longitude;
                mRoutePoints.add(new LocationData(new LatLng(newPointLat, newPointLng),
                        mTotalSecond));

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
}
