package com.elegion.tracktor.di.main;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;

import com.elegion.tracktor.ui.common.CustomViewModelFactory;
import com.elegion.tracktor.ui.map.MainViewModel;

import javax.inject.Inject;
import javax.inject.Provider;

public class MainViewModelProvider implements Provider<MainViewModel> {
    AppCompatActivity mActivity;
    CustomViewModelFactory mFactory;

    @Inject
    public MainViewModelProvider(AppCompatActivity activity, CustomViewModelFactory factory) {
        mActivity = activity;
        mFactory = factory;
    }

    @Override
    public MainViewModel get() {
        return ViewModelProviders.of(mActivity, mFactory).get(MainViewModel.class);
    }
}
