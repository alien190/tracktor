package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.elegion.tracktor.data.IRepository;

public class CustomViewModelFactory implements ViewModelProvider.Factory {
    private IRepository mRepository;

    public CustomViewModelFactory(IRepository repository) {
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ResultViewModel(mRepository);
    }
}
