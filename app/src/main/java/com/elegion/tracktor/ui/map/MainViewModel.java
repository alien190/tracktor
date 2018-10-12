package com.elegion.tracktor.ui.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.elegion.tracktor.R;
import com.elegion.tracktor.api.IOpenweathermapApi;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.event.BackgroundPreferencesChangeEvent;
import com.elegion.tracktor.common.event.GoToBackgroundEvent;
import com.elegion.tracktor.common.event.PreferencesChangeEvent;
import com.elegion.tracktor.common.event.ReadyToBackground;
import com.elegion.tracktor.common.event.RequestRouteUpdateEvent;
import com.elegion.tracktor.common.event.RouteUpdateEvent;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.common.event.TimerUpdateEvent;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.model.LocationJobState;
import com.elegion.tracktor.data.model.RealmLocationData;
import com.elegion.tracktor.ui.common.IWeatherViewModel;
import com.elegion.tracktor.utils.CommonUtils;
import com.elegion.tracktor.utils.IDistanceConverter;
import com.elegion.tracktor.utils.StringUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;


//todo выделить погоду в отдельную ViewModel

public class MainViewModel extends ViewModel implements IWeatherViewModel {
    public static final int SERVICE_STATE_UNKNOWN = 0;
    public static final int SERVICE_STATE_RUNNING = 1;
    public static final int SERVICE_STATE_STOPPING = 2;
    public static final int SERVICE_STATE_MUST_RUN = 3;
    public static final int SERVICE_STATE_MUST_STOP = 4;
    public static final int SERVICE_STATE_GO_TO_FOREGROUND = 5;


    private MutableLiveData<Boolean> startEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> stopEnabled = new MutableLiveData<>();
    private MutableLiveData<String> timeText = new MutableLiveData<>();
    private MutableLiveData<String> mDistanceText = new MutableLiveData<>();
    private MutableLiveData<Double> mAverageSpeedLive = new MutableLiveData<>();
    private MutableLiveData<String> mTemperature = new MutableLiveData<>();
    private MutableLiveData<String> mWeatherIconBase64 = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsBigStyleWeather = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsShowWeather = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsWeatherRefreshing = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsScreenshotInProgress = new MutableLiveData<>();
    private MutableLiveData<Integer> mServiceState = new MutableLiveData<>();
    private MutableLiveData<List<LatLng>> mRoutePoints = new MutableLiveData<>();

    private long mTotalTime;
    private double mDistance;
    private double mAverageSpeed;
    private Date mStartDate;
    private SingleObserver mPermissionObserver;
    private Single<Boolean> mIsPermissionGranted = new Single<Boolean>() {
        @Override
        protected void subscribeActual(SingleObserver<? super Boolean> observer) {
            mPermissionObserver = observer;
        }
    };
    private boolean isRouteStart;
    private IRepository mRealmRepository;
    private IDistanceConverter mDistanceConverter;
    private CurrentPreferences mCurrentPreferences;


    public MainViewModel(IRepository repository, CurrentPreferences currentPreferences, IDistanceConverter distanceConverter) {
        Log.d("tracktor_log", "MainViewModel: create");
        EventBus.getDefault().register(this);
        mRealmRepository = repository;
        mDistanceConverter = distanceConverter;
        mCurrentPreferences = currentPreferences;
        mIsShowWeather.postValue(false);
        mIsWeatherRefreshing.postValue(false);
        mIsScreenshotInProgress.postValue(false);
        mServiceState.postValue(SERVICE_STATE_UNKNOWN);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerUpdate(TimerUpdateEvent event) {
        mTotalTime = event.seconds;
        mDistance = event.distance;
        mAverageSpeed = event.averageSpeed;
        mStartDate = event.startDate;
        mTemperature.postValue(StringUtils.getTemperatureText(event.temperature));
        mWeatherIconBase64.postValue(event.weatherIcon);
        mIsShowWeather.postValue(event.weatherIcon != null && !event.weatherIcon.isEmpty());
        timeText.postValue(StringUtils.getDurationText(mTotalTime));
        mDistanceText.postValue(mDistanceConverter.convertDistance(mDistance));
        mAverageSpeedLive.postValue(mAverageSpeed);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPreferencesChange(PreferencesChangeEvent event) {
        mDistanceText.postValue(mDistanceConverter.convertDistance(mDistance));
        mAverageSpeedLive.postValue(mAverageSpeed);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackgroundPreferencesChange(BackgroundPreferencesChangeEvent event) {
        if (mServiceState != null &&
                mServiceState.getValue() != null &&
                mServiceState.getValue() == SERVICE_STATE_RUNNING) {
            if (mCurrentPreferences.getIsBackground()) {
                EventBus.getDefault().post(new GoToBackgroundEvent());
            } else {
                mServiceState.postValue(SERVICE_STATE_GO_TO_FOREGROUND);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadyToBackground(ReadyToBackground event) {
        mServiceState.postValue(SERVICE_STATE_MUST_RUN);
    }


    public void startRoute() {
        isRouteStart = true;
        if (!(mServiceState != null && mServiceState.getValue() != null && mServiceState.getValue() == SERVICE_STATE_RUNNING)) {
            mRealmRepository.deleteLocationJobState();
            mServiceState.postValue(SERVICE_STATE_MUST_RUN);
        }
    }

    public void stopRoute() {
        mIsShowWeather.postValue(false);
        isRouteStart = false;
        if (!(mServiceState != null && mServiceState.getValue() != null && mServiceState.getValue() == SERVICE_STATE_STOPPING)) {
            mServiceState.postValue(SERVICE_STATE_MUST_STOP);
        }
        if (mCurrentPreferences.getIsBackground()) {
            EventBus.getDefault().postSticky(new StopRouteEvent(mRealmRepository.getLocationJobState()));
            mRealmRepository.deleteLocationJobState();
        }
    }

    public void onPermissionGranted() {
        if (mPermissionObserver != null) {
            mPermissionObserver.onSuccess(true);
        } else {
            mIsPermissionGranted = Single.just(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRouteUpdate(RouteUpdateEvent event) {
        mRoutePoints.postValue(event.points);
    }

    @Override
    public LiveData<String> getTemperature() {
        return mTemperature;
    }

    @Override
    public LiveData<String> getWeatherIconBase64() {
        return mWeatherIconBase64;
    }

    @Override
    public LiveData<Boolean> getIsBigStyleWeather() {
        return mIsBigStyleWeather;
    }

    public MutableLiveData<String> getTimeText() {
        return timeText;
    }

    public MutableLiveData<Boolean> getStartEnabled() {
        return startEnabled;
    }

    public MutableLiveData<Boolean> getStopEnabled() {
        return stopEnabled;
    }

    public MutableLiveData<String> getDistanceText() {
        return mDistanceText;
    }

    @Override
    protected void onCleared() {
        EventBus.getDefault().unregister(this);
        Log.d("tracktor_log", "MainViewModel: onCleared");
        super.onCleared();
    }

    public Single<Boolean> getIsPermissionGranted() {
        return mIsPermissionGranted;
    }

    public long saveResults(StopRouteEvent event, String imageBase64) {
        return mRealmRepository.createTrackAndSave(
                event.routeTime,
                event.routeDistance,
                event.averageSpeed,
                event.startDate,
                imageBase64,
                event.temperature,
                event.weatherIcon,
                event.weatherDescription);
    }

    public void routeUpdateRequest() {
        if (mCurrentPreferences.getIsBackground()) {
            LocationJobState state = mRealmRepository.getLocationJobState();
            if (state != null) {
                mRoutePoints.postValue(CommonUtils.locationJobStatePointsToLatLngList(state));
                mTemperature.postValue(StringUtils.getTemperatureText(state.getTemperature()));
                mWeatherIconBase64.postValue(state.getWeatherIcon());
                mIsShowWeather.postValue(state.getWeatherIcon() != null && !state.getWeatherIcon().isEmpty());
                mDistance = state.getDistance();
                mTotalTime = state.getTotalSecond();
                mAverageSpeed = state.getAverageSpeed();
                mStartDate = state.getStartDate();
                mDistanceText.postValue(mDistanceConverter.convertDistance(mDistance));
                timeText.postValue(StringUtils.getDurationText(mTotalTime));
                mAverageSpeedLive.postValue(mAverageSpeed);
            }
        } else {
            EventBus.getDefault().post(new RequestRouteUpdateEvent(null));
        }
    }

    public MutableLiveData<Double> getAverageSpeed() {
        return mAverageSpeedLive;
    }

    public MutableLiveData<Boolean> getIsShowWeather() {
        return mIsShowWeather;
    }

    public MutableLiveData<Boolean> getIsWeatherRefreshing() {
        return mIsWeatherRefreshing;
    }

    public String getLastWeatherIcon() {
        return "";
    }

    public void setLastWeatherIcon(String lastWeatherIcon) {
    }

    public MutableLiveData<Boolean> getIsScreenshotInProgress() {
        return mIsScreenshotInProgress;
    }

    public MutableLiveData<Integer> getServiceState() {
        return mServiceState;
    }

    @Override
    public void updateWeather() {

    }

    public boolean isBackgroundJobRunning() {
        return mRealmRepository.getLocationJobState() != null;
    }

    public MutableLiveData<List<LatLng>> getRoutePoints() {
        return mRoutePoints;
    }
}
