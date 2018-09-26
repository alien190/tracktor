package com.elegion.tracktor.di.application;

import com.elegion.tracktor.api.IOpenweathermapApi;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.common.CustomViewModelFactory;

import javax.inject.Inject;
import javax.inject.Provider;

public class CustomViewModelFactoryProvider implements Provider<CustomViewModelFactory> {

    private IRepository mRepository;
    private IOpenweathermapApi mOpenweathermapApi;

    @Inject
    public CustomViewModelFactoryProvider(IRepository repository, IOpenweathermapApi openweathermapApi) {
        this.mRepository = repository;
        this.mOpenweathermapApi = openweathermapApi;
    }

    @Override
    public CustomViewModelFactory get() {
        return new CustomViewModelFactory(mRepository, mOpenweathermapApi);
    }
}
