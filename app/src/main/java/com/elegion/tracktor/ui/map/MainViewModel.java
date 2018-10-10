package com.elegion.tracktor.ui.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.elegion.tracktor.api.IOpenweathermapApi;
import com.elegion.tracktor.common.event.PreferencesChangeEvent;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.common.event.TimerUpdateEvent;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.common.IWeatherViewModel;
import com.elegion.tracktor.utils.IDistanceConverter;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import io.reactivex.Single;
import io.reactivex.SingleObserver;


//todo выделить погоду в отдельную ViewModel

public class MainViewModel extends ViewModel implements IWeatherViewModel {
    public static final int SERVICE_STATE_UNKNOWN = 0;
    public static final int SERVICE_STATE_RUNNING = 1;
    public static final int SERVICE_STATE_STOPPING = 2;
    public static final int SERVICE_STATE_MUST_RUN = 3;
    public static final int SERVICE_STATE_MUST_STOP = 4;


    private MutableLiveData<Boolean> startEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> stopEnabled = new MutableLiveData<>();
    //private MutableLiveData<Boolean> mIsShutdown = new MutableLiveData<>();
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
    //private IOpenweathermapApi mOpenweathermapApi;
    private IDistanceConverter mDistanceConverter;
    //private Disposable mWeatherDisposable;
//    private int mNormalWeatherUpdateCounter;
//    private int mErrorWeatherUpdateCounter;
//    private boolean mIsSuccessLastWeatherUpdate;
//    private LocationData mLastWeatherUpdateLocation;
//    private double mLastTemperature;
//    private String mLastWeatherIcon;
//    private String mLastWeatherDescription;


    public MainViewModel(IRepository repository, IOpenweathermapApi openweathermapApi, IDistanceConverter distanceConverter) {
        // mIsPermissionGranted.setValue(false);
        Log.d("tracktor_log", "MainViewModel: create");
        EventBus.getDefault().register(this);
        mRealmRepository = repository;
        //  mOpenweathermapApi = openweathermapApi;
        mDistanceConverter = distanceConverter;
        // mIsShutdown.postValue(false);
        mIsShowWeather.postValue(false);
        //mIsSuccessLastWeatherUpdate = false;
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

    //@Subscribe(threadMode = ThreadMode.MAIN)
//    public void onShutdown() {
//       // mIsShutdown.postValue(true);
//        stopRoute();
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onRouteUpdate(SegmentForRouteEvent event) {
//        updateWeatherPeriodically(event.points.second);
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onStartRoute(StartRouteEvent event) {
//        updateWeatherPeriodically(event.firstPoint);
//    }

//    private void updateWeatherPeriodically(LocationData locationData) {
//        if ((mIsSuccessLastWeatherUpdate &&
//                mTotalTime >= BuildConfig.NORMAL_WEATHER_UPDATE_PERIOD_SECS * mNormalWeatherUpdateCounter) ||
//                (!mIsSuccessLastWeatherUpdate &&
//                        mTotalTime >= BuildConfig.ERROR_WEATHER_UPDATE_PERIOD_SECS * mErrorWeatherUpdateCounter)) {
//            updateWeather(locationData);
//        }
//    }

//    private void updateWeather(LocationData locationData) {
//        if (mWeatherDisposable != null) {
//            mWeatherDisposable.dispose();
//        }
//        mWeatherDisposable = mOpenweathermapApi.getWeather(locationData.point.latitude,
//                locationData.point.longitude)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe(disposable -> mIsWeatherRefreshing.postValue(true))
//                .doFinally(() -> mIsWeatherRefreshing.postValue(false))
//                .subscribe(this::setWeather, this::weatherUpdateError);
//        mLastWeatherUpdateLocation = locationData;
//        mNormalWeatherUpdateCounter = ((int) mTotalTime / BuildConfig.NORMAL_WEATHER_UPDATE_PERIOD_SECS) + 1;
//        mErrorWeatherUpdateCounter = ((int) mTotalTime / BuildConfig.ERROR_WEATHER_UPDATE_PERIOD_SECS) + 1;
//    }

//    public void updateWeather() {
//        if (mLastWeatherUpdateLocation != null)
//            updateWeather(mLastWeatherUpdateLocation);
//    }
//
//    private void setWeather(Weather weather) {
//        mIsSuccessLastWeatherUpdate = true;
//        mLastTemperature = weather.getMain().getTemp();
//        mTemperature.postValue(StringUtils.getTemperatureText(mLastTemperature));
//        List<WeatherItem> weatherItems = weather.getWeather();
//        if (weatherItems != null && !weatherItems.isEmpty()) {
//            WeatherItem item = weatherItems.get(0);
//            mWeatherIconBase64.postValue(StringUtils.getWeatherIconBase64(item.getIcon()));
//            mLastWeatherDescription = item.getDescription();
//        }
//        if (mIsShowWeather.getValue() != null && !mIsShowWeather.getValue()) {
//            mIsShowWeather.postValue(true);
//        }
//    }
//
//    private void weatherUpdateError(Throwable throwable) {
//        mIsSuccessLastWeatherUpdate = false;
//        throwable.printStackTrace();
//    }

    public void startRoute() {

        isRouteStart = true;
        if (!(mServiceState != null && mServiceState.getValue() != null && mServiceState.getValue() == SERVICE_STATE_RUNNING)) {
            mServiceState.postValue(SERVICE_STATE_MUST_RUN);
        }
        //startEnabled.postValue(false);
        //stopEnabled.postValue(true);
    }

    public void stopRoute() {
        mIsShowWeather.postValue(false);
        isRouteStart = false;
        if (!(mServiceState != null && mServiceState.getValue() != null && mServiceState.getValue() == SERVICE_STATE_STOPPING)) {
            mServiceState.postValue(SERVICE_STATE_MUST_STOP);
        }
        //startEnabled.postValue(true);
        //stopEnabled.postValue(false);
    }

    public void onPermissionGranted() {
        if (mPermissionObserver != null) {
            mPermissionObserver.onSuccess(true);
        } else {
            mIsPermissionGranted = Single.just(true);
        }
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

//    public MutableLiveData<Boolean> getIsShutdown() {
//        return mIsShutdown;
//    }

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
}
