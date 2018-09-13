package com.elegion.tracktor.di.main;

import android.support.v7.app.AppCompatActivity;

import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.data.RealmRepository;
import com.elegion.tracktor.ui.result.CustomViewModelFactory;

import toothpick.config.Module;

public class MainModule extends Module{
    private RealmRepository mRealmRepository = new RealmRepository();
    private AppCompatActivity mActivity;



    public MainModule(AppCompatActivity activity) {
        mActivity = activity;

        bind(AppCompatActivity.class).toInstance(mActivity);
        bind(IRepository.class).toInstance(mRealmRepository);
        bind(CustomViewModelFactory.class).toProvider(CustomViewModelFactoryProvider.class).providesSingletonInScope();

    }
}
