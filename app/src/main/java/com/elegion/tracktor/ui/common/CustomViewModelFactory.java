package com.elegion.tracktor.ui.common;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.elegion.tracktor.api.IOpenweathermapApi;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.result.ResultDetailsViewModel;
import com.elegion.tracktor.ui.result.ResultViewModel;
import com.elegion.tracktor.utils.DistanceConverter;
import com.elegion.tracktor.utils.IDistanceConverter;

public class CustomViewModelFactory implements ViewModelProvider.Factory {
    private IRepository mRepository;
    private IOpenweathermapApi mOpenweathermapApi;
    private IDistanceConverter mDistanceConverter;

    public CustomViewModelFactory(IRepository repository, IOpenweathermapApi openweathermapApi,
                                  IDistanceConverter distanceConverter) {
        this.mRepository = repository;
        this.mOpenweathermapApi = openweathermapApi;
        this.mDistanceConverter = distanceConverter;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == MainViewModel.class) {
            return (T) new MainViewModel(mRepository, mOpenweathermapApi, mDistanceConverter);
        }
        return (T) new ResultViewModel(mRepository);
    }
}
