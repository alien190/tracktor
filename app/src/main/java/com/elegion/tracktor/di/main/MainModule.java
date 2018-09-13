package com.elegion.tracktor.di.main;

import android.support.v7.app.AppCompatActivity;

import toothpick.config.Module;

public class MainModule extends Module{

    private AppCompatActivity mActivity;



    public MainModule(AppCompatActivity activity) {
        mActivity = activity;
        bind(AppCompatActivity.class).toInstance(mActivity);
    }
}
