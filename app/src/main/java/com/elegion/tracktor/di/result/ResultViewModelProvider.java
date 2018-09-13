package com.elegion.tracktor.di.result;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;

import com.elegion.tracktor.ui.common.CustomViewModelFactory;
import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.result.ResultViewModel;

import javax.inject.Inject;
import javax.inject.Provider;

public class ResultViewModelProvider implements Provider<ResultViewModel> {
    AppCompatActivity mActivity;
    CustomViewModelFactory mFactory;

    @Inject
    public ResultViewModelProvider(AppCompatActivity activity, CustomViewModelFactory factory) {
        mActivity = activity;
        mFactory = factory;
    }

    @Override
    public ResultViewModel get() {
        return ViewModelProviders.of(mActivity, mFactory).get(ResultViewModel.class);
    }
}
