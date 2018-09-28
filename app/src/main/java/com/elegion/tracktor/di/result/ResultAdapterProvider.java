package com.elegion.tracktor.di.result;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.ui.result.ResultAdapter;
import com.elegion.tracktor.utils.IDistanceConverter;

import javax.inject.Inject;
import javax.inject.Provider;

public class ResultAdapterProvider implements Provider<ResultAdapter> {
    private CurrentPreferences mCurrentPreferences;
    private IDistanceConverter mDistanceConverter;

    @Inject
    public ResultAdapterProvider(CurrentPreferences currentPreferences, IDistanceConverter distanceConverter) {
        mCurrentPreferences = currentPreferences;
        mDistanceConverter = distanceConverter;
    }

    @Override
    public ResultAdapter get() {
        return new ResultAdapter(mCurrentPreferences, mDistanceConverter);
    }
}
