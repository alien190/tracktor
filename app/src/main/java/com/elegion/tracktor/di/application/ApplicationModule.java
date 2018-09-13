package com.elegion.tracktor.di.application;

import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.di.main.CustomViewModelFactoryProvider;
import com.elegion.tracktor.ui.common.CustomViewModelFactory;

import toothpick.config.Module;

public class ApplicationModule extends Module {
    private RealmRepository mRealmRepository = new RealmRepository();

    public ApplicationModule() {
        bind(IRepository.class).toInstance(mRealmRepository);
        bind(CustomViewModelFactory.class).toProvider(CustomViewModelFactoryProvider.class).providesSingletonInScope();
    }
}
