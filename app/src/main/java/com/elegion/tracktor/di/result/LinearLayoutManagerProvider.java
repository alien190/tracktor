package com.elegion.tracktor.di.result;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.ui.result.ResultAdapter;
import com.elegion.tracktor.utils.IDistanceConverter;

import javax.inject.Inject;
import javax.inject.Provider;

public class LinearLayoutManagerProvider implements Provider<LinearLayoutManager> {
    private Context mContext;

    @Inject
    public LinearLayoutManagerProvider(Context context) {
        mContext = context;
    }

    @Override
    public LinearLayoutManager get() {
        return new LinearLayoutManager(mContext);
    }
}
