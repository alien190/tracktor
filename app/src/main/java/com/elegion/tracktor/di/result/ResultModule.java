package com.elegion.tracktor.di.result;

import android.support.v7.app.AppCompatActivity;

import com.elegion.tracktor.App;
import com.elegion.tracktor.di.main.MainViewModelProvider;
import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.result.ResultActivity;
import com.elegion.tracktor.ui.result.ResultViewModel;

import toothpick.config.Module;

public class ResultModule extends Module {
    private AppCompatActivity mActivity;

    public ResultModule(AppCompatActivity activity) {
        mActivity = activity;
        bind(AppCompatActivity.class).toInstance(mActivity);
        bind(ResultViewModel.class).toProvider(ResultViewModelProvider.class);
    }
}
