package com.elegion.tracktor.ui.common;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.elegion.tracktor.api.IOpenweathermapApi;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.result.ResultDetailsViewModel;
import com.elegion.tracktor.ui.result.ResultViewModel;

public class CustomViewModelFactory implements ViewModelProvider.Factory {
    private IRepository mRepository;
    private IOpenweathermapApi mOpenweathermapApi;

    public CustomViewModelFactory(IRepository repository, IOpenweathermapApi openweathermapApi) {
        this.mRepository = repository;
        this.mOpenweathermapApi = openweathermapApi;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == MainViewModel.class) {
            return (T) new MainViewModel(mRepository, mOpenweathermapApi);
        }
        return (T) new ResultViewModel(mRepository);
    }
}
