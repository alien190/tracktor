package com.elegion.tracktor.ui.common;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

public interface IWeatherViewModel {
    LiveData<String> getTemperature();
    LiveData<String> getWeatherPictureURL();
    LiveData<Boolean> getIsBigStyleWeather();
    LiveData<Boolean> getIsShowWeather();
}
