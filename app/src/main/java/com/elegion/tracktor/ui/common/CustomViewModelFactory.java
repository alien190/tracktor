package com.elegion.tracktor.ui.common;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.result.ResultDetailsViewModel;
import com.elegion.tracktor.ui.result.ResultViewModel;

public class CustomViewModelFactory implements ViewModelProvider.Factory {
    private IRepository mRepository;

    public CustomViewModelFactory(IRepository repository) {
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == MainViewModel.class) {
            return (T) new MainViewModel(mRepository);
        }
        return (T) new ResultViewModel(mRepository);
    }
}
