package com.elegion.tracktor.di.resultDetails;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;

import com.elegion.tracktor.ui.result.ResultDetailsViewModel;
import com.elegion.tracktor.ui.result.ResultDetailsViewModelFactory;

import javax.inject.Inject;
import javax.inject.Provider;

public class ResultDetailsViewModelProvider implements Provider<ResultDetailsViewModel> {
    Fragment mFragment;
    ResultDetailsViewModelFactory mFactory;

    @Inject
    public ResultDetailsViewModelProvider(Fragment fragment, ResultDetailsViewModelFactory factory) {
        mFactory = factory;
        mFragment = fragment;
    }

    @Override
    public ResultDetailsViewModel get() {
        return ViewModelProviders.of(mFragment, mFactory).get(ResultDetailsViewModel.class);
    }
}
