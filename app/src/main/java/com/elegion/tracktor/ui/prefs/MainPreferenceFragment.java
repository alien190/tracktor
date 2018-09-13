package com.elegion.tracktor.ui.prefs;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.elegion.tracktor.R;

public class MainPreferenceFragment extends PreferenceFragmentCompat {

    public static MainPreferenceFragment newInstance() {

        Bundle args = new Bundle();

        MainPreferenceFragment fragment = new MainPreferenceFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
