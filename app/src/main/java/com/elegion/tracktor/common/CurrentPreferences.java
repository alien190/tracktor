package com.elegion.tracktor.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.event.PreferencesChangeEvent;
import com.elegion.tracktor.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class CurrentPreferences {
    private Map mPrefs;
    private Integer[] mKeys = {R.string.sex_key, R.string.weight_key, R.string.height_key};
    private String weightKey;
    private String heightKey;
    private List<String> mActions;


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

        weightKey = context.getString(R.string.weight_key);
        heightKey = context.getString(R.string.height_key);

        mActions = new ArrayList<>(Arrays.asList(
                context.getResources().getStringArray(R.array.actions)));
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

    public double getWeight() {
        double value = getDoubleValue(weightKey);
        return value != 0 ? value : 60;
    }

    public double getHeight() {
        double value = getDoubleValue(heightKey);
        return value != 0 ? value : 175;
    }

    private double getDoubleValue(String key) {
        Object value = mPrefs.get(key);
        if (value != null) {
            try {
                return Double.valueOf(value.toString());
            } catch (Throwable t) {
                return 0;
            }
        } else {
            return 0;
        }
    }
    public List<String> getActions() {
        return mActions;
    }
}
