package com.elegion.tracktor.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.event.PreferencesChangeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class CurrentPreferences {
    private Map mPrefs;
    private Integer[] mKeys = {R.string.sex_key};


    public void notifyChanges() {
        EventBus.getDefault().post(new PreferencesChangeEvent());
    }

    public void init(Context context) {
        mPrefs = new HashMap<String, String>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (int id : mKeys) {
            String key = context.getString(id);
            mPrefs.put(key, sharedPreferences.getString(key, ""));
        }
    }

    public void setValueAndNotify(String key, String value) {
        Object oldValue = mPrefs.get(key);
        if (oldValue == null || !oldValue.toString().equals(value)) {
            mPrefs.put(key, value);
            notifyChanges();
        }
    }

    public String getValue(String key) {
        Object value = mPrefs.get(key);
        return value != null ? value.toString() : "";
    }
}
