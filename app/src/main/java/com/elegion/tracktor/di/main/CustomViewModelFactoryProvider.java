package com.elegion.tracktor.di.main;

import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.result.CustomViewModelFactory;

import javax.inject.Inject;
import javax.inject.Provider;

public class CustomViewModelFactoryProvider implements Provider<CustomViewModelFactory> {

    private IRepository mRepository;

    @Inject
    public CustomViewModelFactoryProvider(IRepository repository) {
        mRepository = repository;
    }

    @Override
    public CustomViewModelFactory get() {
        return new CustomViewModelFactory(mRepository);
    }
}
