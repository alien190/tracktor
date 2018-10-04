package com.elegion.tracktor.ui.prefs;

import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;

public class TrackLinePreferencesDialogFragment extends PreferenceDialogFragmentCompat {

    public static TrackLinePreferencesDialogFragment newInstance(String key) {

        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        TrackLinePreferencesDialogFragment fragment = new TrackLinePreferencesDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {

    }
}
