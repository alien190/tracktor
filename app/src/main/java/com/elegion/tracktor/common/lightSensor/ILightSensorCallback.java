package com.elegion.tracktor.common.lightSensor;

import android.arch.lifecycle.LifecycleOwner;

public interface ILightSensorCallback extends LifecycleOwner {
    void onChangeState(boolean isDark);
}
