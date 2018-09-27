package com.elegion.tracktor.ui.common;

import android.arch.lifecycle.LiveData;

public interface IWeatherViewModel {
    LiveData<String> getTemperature();

    LiveData<String> getWeatherPictureURL();

    LiveData<Boolean> getIsBigStyleWeather();

    LiveData<Boolean> getIsShowWeather();

    LiveData<Boolean> getIsWeatherRefreshing();

    void updateWeather();
}
