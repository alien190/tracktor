package com.elegion.tracktor.ui.prefs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.elegion.tracktor.R;

public class MainPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static MainPreferenceFragment newInstance() {

        Bundle args = new Bundle();

        MainPreferenceFragment fragment = new MainPreferenceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    private void initSummary(){
        setSummaryFor(findPreference(getString(R.string.unit_key)));
        setSummaryFor(findPreference(getString(R.string.shutdown_key)));
        setSummaryFor(findPreference(getString(R.string.weight_key)));
        setSummaryFor(findPreference(getString(R.string.sex_key)));
        setSummaryFor(findPreference(getString(R.string.age_key)));
        setSummaryFor(findPreference(getString(R.string.height_key)));
    }

    private void setSummaryFor(Preference preference) {
        if (preference instanceof ListPreference) {
            preference.setSummary(((ListPreference) preference).getEntry());
        } else if (preference instanceof EditTextPreference) {
            preference.setSummary(((EditTextPreference) preference).getText());
        }
    }
}
