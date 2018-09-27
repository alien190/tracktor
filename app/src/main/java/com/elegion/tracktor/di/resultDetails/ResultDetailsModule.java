package com.elegion.tracktor.di.resultDetails;

import android.support.v4.app.Fragment;

import com.elegion.tracktor.ui.common.ICommentViewModel;
import com.elegion.tracktor.ui.common.IWeatherViewModel;
import com.elegion.tracktor.ui.result.ResultDetailsViewModel;
import com.elegion.tracktor.ui.result.ResultDetailsViewModelFactory;
import com.elegion.tracktor.ui.weather.WeatherFragment;

import toothpick.config.Module;

public class ResultDetailsModule extends Module {
    private Fragment mFragment;
    private long mId;

    public ResultDetailsModule(Fragment fragment, long id) {
        mFragment = fragment;
        mId = id;
        bind(Fragment.class).toInstance(mFragment);
        bind(Long.class).withName("TRACK_ID").toInstance(mId);
        bind(ResultDetailsViewModel.class).toProvider(ResultDetailsViewModelProvider.class).providesSingletonInScope();
        bind(ICommentViewModel.class).toProvider(ResultDetailsViewModelProvider.class).providesSingletonInScope();
        bind(ResultDetailsViewModelFactory.class).toProvider(ResultDetailsViewModelFactoryProvider.class).providesSingletonInScope();
        bind(IWeatherViewModel.class).toProvider(ResultDetailsViewModelProvider.class).providesSingletonInScope();
        bind(WeatherFragment.class).toInstance(WeatherFragment.newInstance(false));
    }
}
