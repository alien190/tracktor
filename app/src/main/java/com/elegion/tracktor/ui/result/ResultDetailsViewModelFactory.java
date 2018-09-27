package com.elegion.tracktor.ui.result;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.map.MainViewModel;
import com.elegion.tracktor.ui.messageTemplate.MessageTemplate;

public class ResultDetailsViewModelFactory implements ViewModelProvider.Factory {
    private IRepository mRepository;
    private CurrentPreferences mCurrentPreferences;
    private MessageTemplate mMessageTemplate;
    private Long mId;

    public ResultDetailsViewModelFactory(IRepository repository,
                                         CurrentPreferences currentPreferences,
                                         MessageTemplate messageTemplate,
                                         Long id) {
        this.mRepository = repository;
        this.mCurrentPreferences = currentPreferences;
        this.mMessageTemplate = messageTemplate;
        this.mId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ResultDetailsViewModel(mRepository, mCurrentPreferences, mMessageTemplate, mId);
    }
}
