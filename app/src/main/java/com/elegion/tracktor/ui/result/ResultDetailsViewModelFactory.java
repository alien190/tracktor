package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;
import com.elegion.tracktor.utils.IDistanceConverter;

public class ResultDetailsViewModelFactory implements ViewModelProvider.Factory {
    private IRepository mRepository;
    private CurrentPreferences mCurrentPreferences;
    private IDistanceConverter mIDistanceConverter;
    private MessageTemplate mMessageTemplate;
    private Long mId;

    public ResultDetailsViewModelFactory(IRepository repository,
                                         CurrentPreferences currentPreferences,
                                         IDistanceConverter distanceConverter,
                                         MessageTemplate messageTemplate,
                                         Long id) {
        this.mRepository = repository;
        this.mCurrentPreferences = currentPreferences;
        this.mIDistanceConverter = distanceConverter;
        this.mMessageTemplate = messageTemplate;
        this.mId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ResultDetailsViewModel(mRepository, mCurrentPreferences,
                mIDistanceConverter, mMessageTemplate, mId);
    }
}
