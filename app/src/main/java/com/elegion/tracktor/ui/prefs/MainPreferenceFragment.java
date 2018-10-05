package com.elegion.tracktor.ui.prefs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

public class MainPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    CurrentPreferences mCurrentPreferences;

    public static MainPreferenceFragment newInstance() {

        Bundle args = new Bundle();

        MainPreferenceFragment fragment = new MainPreferenceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Scope scope = Toothpick.openScope("Application");
        Toothpick.inject(this, scope);
        initSummary();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setSummaryFor(findPreference(key));
    }

    private void initSummary() {
        setSummaryFor(findPreference(getString(R.string.unit_key)));
        setSummaryFor(findPreference(getString(R.string.shutdown_key)));
        setSummaryFor(findPreference(getString(R.string.weight_key)));
        setSummaryFor(findPreference(getString(R.string.sex_key)));
        setSummaryFor(findPreference(getString(R.string.age_key)));
        setSummaryFor(findPreference(getString(R.string.height_key)));
        setSummaryFor(findPreference(getString(R.string.picture_quality_key)));
        setSummaryFor(findPreference(getString(R.string.map_theme_key)));
    }

    private void setSummaryFor(Preference preference) {
        String value = "";
        if (preference instanceof ListPreference) {
            value = String.valueOf(((ListPreference) preference).getValue());
            preference.setSummary(((ListPreference) preference).getEntry());
        } else if (preference instanceof EditTextPreference) {
            value = String.valueOf(((EditTextPreference) preference).getText());
            preference.setSummary(((EditTextPreference) preference).getText());
        } else if (preference instanceof TrackLinePreference) {
            value = ((TrackLinePreference) preference).getValue();
        }
        mCurrentPreferences.setValueAndNotify(preference.getKey(), value);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof TrackLinePreference) {
            DialogFragment dialogFragment =
                    TrackLinePreferencesDialogFragment.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), "TrackLinePreferencesDialogFragment");
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
