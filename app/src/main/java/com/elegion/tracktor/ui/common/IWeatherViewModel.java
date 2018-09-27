package com.elegion.tracktor.ui.common;

import android.arch.lifecycle.LiveData;

public interface IWeatherViewModel {
    LiveData<String> getTemperature();

    LiveData<String> getWeatherIconURL();

    LiveData<Boolean> getIsBigStyleWeather();

    LiveData<Boolean> getIsShowWeather();

    LiveData<Boolean> getIsWeatherRefreshing();

    void updateWeather();

    void setLastWeatherIcon(String iconBase64);
}
