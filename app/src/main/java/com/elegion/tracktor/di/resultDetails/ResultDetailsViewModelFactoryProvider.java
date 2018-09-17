package com.elegion.tracktor.di.resultDetails;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.common.CustomViewModelFactory;
import com.elegion.tracktor.ui.result.ResultDetailsViewModelFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class ResultDetailsViewModelFactoryProvider implements Provider<ResultDetailsViewModelFactory> {

    private IRepository mRepository;
    private CurrentPreferences mCurrentPreferences;
    private Long mId;

    @Inject
    public ResultDetailsViewModelFactoryProvider(IRepository repository,
                                                 CurrentPreferences currentPreferences,
                                                 @Named("TRACK_ID") Long id) {
        mRepository = repository;
        mCurrentPreferences = currentPreferences;
        mId = id;
    }

    @Override
    public ResultDetailsViewModelFactory get() {
        return new ResultDetailsViewModelFactory(mRepository, mCurrentPreferences, mId);
    }
}