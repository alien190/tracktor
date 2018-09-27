package com.elegion.tracktor.ui.map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.elegion.tracktor.BuildConfig;
import com.elegion.tracktor.api.IOpenweathermapApi;
import com.elegion.tracktor.api.model.Weather;
import com.elegion.tracktor.api.model.WeatherItem;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.common.event.ShutdownEvent;
import com.elegion.tracktor.common.event.StartRouteEvent;
import com.elegion.tracktor.common.event.TimerUpdateEvent;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.common.IWeatherViewModel;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel implements IWeatherViewModel {
    private MutableLiveData<Boolean> startEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> stopEnabled = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsShutdown = new MutableLiveData<>();
    private MutableLiveData<String> timeText = new MutableLiveData<>();
    private MutableLiveData<String> mDistanceText = new MutableLiveData<>();
    private MutableLiveData<Double> mAverageSpeedLive = new MutableLiveData<>();
    private MutableLiveData<String> mTemperature = new MutableLiveData<>();
    private MutableLiveData<String> mWeatherIconURL = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsBigStyleWeather = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsShowWeather = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsWeatherRefreshing = new MutableLiveData<>();

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
    private IOpenweathermapApi mOpenweathermapApi;
    private Disposable mWeatherDisposable;
    private int mNormalWeatherUpdateCounter;
    private int mErrorWeatherUpdateCounter;
    private boolean mIsSuccessLastWeatherUpdate;
    private LocationData mLastWeatherUpdateLocation;
    private double mLastTemperature;
    private String mLastWeatherIcon;


    public MainViewModel(IRepository repository, IOpenweathermapApi openweathermapApi) {
        // mIsPermissionGranted.setValue(false);
        EventBus.getDefault().register(this);
        mRealmRepository = repository;
        mOpenweathermapApi = openweathermapApi;
        mIsShutdown.postValue(false);
        mIsShowWeather.postValue(false);
        mIsSuccessLastWeatherUpdate = false;
        mIsWeatherRefreshing.postValue(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimerUpdate(TimerUpdateEvent event) {
        mTotalTime = event.seconds;
        mDistance = event.distance;
        mAverageSpeed = event.averageSpeed;
        mStartDate = event.startDate;

        timeText.postValue(StringUtils.getDurationText(mTotalTime));
        mDistanceText.postValue(StringUtils.getDistanceText(mDistance));
        mAverageSpeedLive.postValue(mAverageSpeed);
        if (!isRouteStart) {
            startRoute();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShutdown(ShutdownEvent event) {
        mIsShutdown.postValue(true);
        stopRoute();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRouteUpdate(SegmentForRouteEvent event) {
        updateWeatherPeriodically(event.points.second);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartRoute(StartRouteEvent event) {
        updateWeatherPeriodically(event.firstPoint);
    }

    private void updateWeatherPeriodically(LocationData locationData) {
        if ((mIsSuccessLastWeatherUpdate &&
                mTotalTime >= BuildConfig.NORMAL_WEATHER_UPDATE_PERIOD_SECS * mNormalWeatherUpdateCounter) ||
                (!mIsSuccessLastWeatherUpdate &&
                        mTotalTime >= BuildConfig.ERROR_WEATHER_UPDATE_PERIOD_SECS * mErrorWeatherUpdateCounter)) {
            updateWeather(locationData);
        }
    }

    private void updateWeather(LocationData locationData) {
        if (mWeatherDisposable != null) {
            mWeatherDisposable.dispose();
        }
        mWeatherDisposable = mOpenweathermapApi.getWeather(locationData.point.latitude,
                locationData.point.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mIsWeatherRefreshing.postValue(true))
                .doFinally(() -> mIsWeatherRefreshing.postValue(false))
                .subscribe(this::setWeather, this::weatherUpdateError);
        mLastWeatherUpdateLocation = locationData;
        mNormalWeatherUpdateCounter = ((int) mTotalTime / BuildConfig.NORMAL_WEATHER_UPDATE_PERIOD_SECS) + 1;
        mErrorWeatherUpdateCounter = ((int) mTotalTime / BuildConfig.ERROR_WEATHER_UPDATE_PERIOD_SECS) + 1;
    }

    public void updateWeather() {
        if (mLastWeatherUpdateLocation != null)
            updateWeather(mLastWeatherUpdateLocation);
    }

    private void setWeather(Weather weather) {
        mIsSuccessLastWeatherUpdate = true;
        mLastTemperature = weather.getMain().getTemp();
        mTemperature.postValue(StringUtils.getTemperatureText(mLastTemperature));
        List<WeatherItem> weatherItems = weather.getWeather();
        if (weatherItems != null && !weatherItems.isEmpty()) {
            mLastWeatherIcon = weatherItems.get(0).getIcon();
            mWeatherIconURL.postValue(StringUtils.getWeatherIconURL(mLastWeatherIcon));
        }
        if (mIsShowWeather.getValue() != null && !mIsShowWeather.getValue()) {
            mIsShowWeather.postValue(true);
        }
    }

    private void weatherUpdateError(Throwable throwable) {
        mIsSuccessLastWeatherUpdate = false;
        throwable.printStackTrace();
    }

    public void startRoute() {
        mNormalWeatherUpdateCounter = 0;
        mErrorWeatherUpdateCounter = 0;
        isRouteStart = true;
        startEnabled.postValue(false);
        stopEnabled.postValue(true);
    }

    public void stopRoute() {
        mIsShowWeather.postValue(false);
        isRouteStart = false;
        startEnabled.postValue(true);
        stopEnabled.postValue(false);
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
    public LiveData<String> getWeatherPictureURL() {
        return mWeatherIconURL;
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
        super.onCleared();
    }

    public Single<Boolean> getIsPermissionGranted() {
        return mIsPermissionGranted;
    }

    public long saveResults(String imageBase64) {
        return mRealmRepository.createTrackAndSave(
                mTotalTime,
                mDistance,
                mAverageSpeed,
                mStartDate,
                imageBase64,
                mLastTemperature,
                mLastWeatherIcon);
    }

    public MutableLiveData<Boolean> getIsShutdown() {
        return mIsShutdown;
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
}
