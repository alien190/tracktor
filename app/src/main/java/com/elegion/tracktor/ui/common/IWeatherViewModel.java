package com.elegion.tracktor.ui.common;

import android.arch.lifecycle.LiveData;

public interface IWeatherViewModel {
    LiveData<String> getDegrees();
    LiveData<String> getPictureURL();
    LiveData<Boolean> isBig();
}
