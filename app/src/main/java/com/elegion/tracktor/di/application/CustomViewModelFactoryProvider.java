package com.elegion.tracktor.di.application;

import com.elegion.tracktor.api.IOpenweathermapApi;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.common.CustomViewModelFactory;
import com.elegion.tracktor.utils.IDistanceConverter;

import javax.inject.Inject;
import javax.inject.Provider;

public class CustomViewModelFactoryProvider implements Provider<CustomViewModelFactory> {

    private IRepository mRepository;
    private CurrentPreferences mCurrentPreferences;
    private IDistanceConverter mDistanceConverter;

    @Inject
    public CustomViewModelFactoryProvider(IRepository repository, CurrentPreferences currentPreferences,
                                          IDistanceConverter distanceConverter) {
        this.mRepository = repository;
        this.mCurrentPreferences = currentPreferences;
        this.mDistanceConverter = distanceConverter;
    }

    @Override
    public CustomViewModelFactory get() {
        return new CustomViewModelFactory(mRepository, mCurrentPreferences, mDistanceConverter);
    }
}
