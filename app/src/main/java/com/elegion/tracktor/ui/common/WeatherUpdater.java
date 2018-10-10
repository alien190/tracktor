package com.elegion.tracktor.ui.common;

import com.elegion.tracktor.BuildConfig;
import com.elegion.tracktor.api.IOpenweathermapApi;
import com.elegion.tracktor.api.model.WeatherItem;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.utils.StringUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WeatherUpdater {
    private IOpenweathermapApi mOpenweathermapApi;
    private boolean mIsSuccessLastWeatherUpdate;
    private LocationData mLastWeatherUpdateLocation;
    private double mLastTemperature;
   // private String mLastTemperatureText;

    private String mLastWeatherDescription;
    private int mNormalWeatherUpdateCounter;
    private int mErrorWeatherUpdateCounter;
    private Disposable mWeatherDisposable;
    private String mWeatherIconURL;
    private String mLastWeatherIconBase64;

    public WeatherUpdater(IOpenweathermapApi openweathermapApi) {
        mOpenweathermapApi = openweathermapApi;
        mNormalWeatherUpdateCounter = 0;
        mErrorWeatherUpdateCounter = 0;
    }

    public void updateWeatherPeriodically(LocationData locationData) {
        if ((mIsSuccessLastWeatherUpdate &&
                locationData.timeSeconds >= BuildConfig.NORMAL_WEATHER_UPDATE_PERIOD_SECS * mNormalWeatherUpdateCounter) ||
                (!mIsSuccessLastWeatherUpdate &&
                        locationData.timeSeconds >= BuildConfig.ERROR_WEATHER_UPDATE_PERIOD_SECS * mErrorWeatherUpdateCounter)) {
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
                .subscribe(this::setWeather, this::weatherUpdateError);
        mLastWeatherUpdateLocation = locationData;
        mNormalWeatherUpdateCounter = ((int) locationData.timeSeconds / BuildConfig.NORMAL_WEATHER_UPDATE_PERIOD_SECS) + 1;
        mErrorWeatherUpdateCounter = ((int) locationData.timeSeconds / BuildConfig.ERROR_WEATHER_UPDATE_PERIOD_SECS) + 1;
    }
    public void updateWeather() {
        if (mLastWeatherUpdateLocation != null)
            updateWeatherPeriodically(mLastWeatherUpdateLocation);
    }

    private void setWeather(com.elegion.tracktor.api.model.Weather weather) {
        mIsSuccessLastWeatherUpdate = true;
        mLastTemperature = weather.getMain().getTemp();
       // mLastTemperatureText = StringUtils.getTemperatureText(mLastTemperature);
        List<WeatherItem> weatherItems = weather.getWeather();
        if (weatherItems != null && !weatherItems.isEmpty()) {
            WeatherItem item = weatherItems.get(0);
            mWeatherIconURL = StringUtils.getWeatherIconURL(item.getIcon());

            mLastWeatherDescription = item.getDescription();
        }
    }

    private void weatherUpdateError(Throwable throwable) {
        mIsSuccessLastWeatherUpdate = false;
        throwable.printStackTrace();
    }

    public Double getTemperature() {
        return mLastTemperature;
    }

    public String getWeatherIcon() {
        return mLastWeatherIcon;
    }

    public String getWeatherDescription() {
        return mLastWeatherDescription;
    }
}
