package com.elegion.tracktor.di.resultDetails;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.common.CustomViewModelFactory;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.elegion.tracktor.ui.result.ResultDetailsViewModelFactory;
import com.elegion.tracktor.utils.IDistanceConverter;

import java.util.IdentityHashMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

public class ResultDetailsViewModelFactoryProvider implements Provider<ResultDetailsViewModelFactory> {

    private IRepository mRepository;
    private CurrentPreferences mCurrentPreferences;
    private IDistanceConverter mDistanceConverter;
    private MessageTemplate mMessageTemplate;
    private Long mId;

    @Inject
    public ResultDetailsViewModelFactoryProvider(IRepository repository,
                                                 CurrentPreferences currentPreferences,
                                                 IDistanceConverter distanceConverter,
                                                 MessageTemplate messageTemplate,
                                                 @Named("TRACK_ID") Long id) {
        this.mRepository = repository;
        this.mCurrentPreferences = currentPreferences;
        this.mDistanceConverter = distanceConverter;
        this.mMessageTemplate = messageTemplate;
        this.mId = id;
    }

    @Override
    public ResultDetailsViewModelFactory get() {
        return new ResultDetailsViewModelFactory(mRepository, mCurrentPreferences,
                mDistanceConverter, mMessageTemplate, mId);
    }
}
