package com.elegion.tracktor.di.application;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.utils.DistanceConverter;

import javax.inject.Inject;
import javax.inject.Provider;

public class DistanceConverterProvider implements Provider<DistanceConverter> {

    private CurrentPreferences mCurrentPreferences;

    @Inject
    public DistanceConverterProvider(CurrentPreferences currentPreferences) {
        this.mCurrentPreferences = currentPreferences;

    }

    @Override
    public DistanceConverter get() {
        return new DistanceConverter(mCurrentPreferences);
    }
}
