package com.elegion.tracktor.di.application;

import com.elegion.tracktor.api.IOpenweathermapApi;
import com.elegion.tracktor.ui.common.WeatherUpdater;

import javax.inject.Inject;
import javax.inject.Provider;

public class WeatherUpdateProvider implements Provider<WeatherUpdater> {

    private IOpenweathermapApi mOpenweathermapApi;

    @Inject
    public WeatherUpdateProvider(IOpenweathermapApi openweathermapApi) {
        mOpenweathermapApi = openweathermapApi;
    }

    @Override
    public WeatherUpdater get() {
        return new WeatherUpdater(mOpenweathermapApi);
    }
}
