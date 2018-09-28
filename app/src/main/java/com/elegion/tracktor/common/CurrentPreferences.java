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
    private Integer[] mKeys = {R.string.sex_key, R.string.weight_key, R.string.height_key, R.string.unit_key};
    private String weightKey;
    private String heightKey;
    private String unitKey;
    private List<String> mDistanceUnitsSi;
    private List<String> mDistanceUnitsEng;
    private List<String> mSpeedUnitsSi;
    private List<String> mSpeedUnitsEng;
    private List<String> mActions;
    private List<String> mMessageTemplateParamTypes;
    private List<String> mMessageTemplatePreviewValues;
    private String mMessageTemplateDraft;
    public static final int UNITS_SI = 1;
    public static final int UNITS_ENG = 2;


    public void notifyChanges() {
        EventBus.getDefault().post(new PreferencesChangeEvent());
    }

    public void init(Context context) {
        initPreferences(context);
        initActions(context);
        initKeys(context);
        initMessageTemplate(context);
        initDistanceUnits(context);
    }

    private void initActions(Context context) {
        mActions = new ArrayList<>(Arrays.asList(
                context.getResources().getStringArray(R.array.actions)));
    }

    private void initPreferences(Context context) {
        mPrefs = new HashMap<String, String>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (int id : mKeys) {
            String key = context.getString(id);
            mPrefs.put(key, sharedPreferences.getString(key, ""));
        }
    }

    private void initKeys(Context context) {
        weightKey = context.getString(R.string.weight_key);
        heightKey = context.getString(R.string.height_key);
        unitKey = context.getString(R.string.unit_key);
    }

    private void initMessageTemplate(Context context) {
        mMessageTemplateParamTypes = new ArrayList<>(Arrays.asList(
                context.getResources().getStringArray(R.array.messageTemplateParameters)));
        mMessageTemplatePreviewValues = new ArrayList<>(Arrays.asList(
                context.getResources().getStringArray(R.array.messageTemplatePreviewValues)));
        mMessageTemplateDraft = context.getString(R.string.messageTemplateDraft);
    }

    private void initDistanceUnits(Context context) {
        mDistanceUnitsSi = new ArrayList<>(Arrays.asList(
                context.getResources().getStringArray(R.array.distanceUnitsSi)));
        mDistanceUnitsEng = new ArrayList<>(Arrays.asList(
                context.getResources().getStringArray(R.array.distanceUnitsEng)));
        mSpeedUnitsSi = new ArrayList<>(Arrays.asList(
                context.getResources().getStringArray(R.array.speedUnitsSi)));
        mSpeedUnitsEng = new ArrayList<>(Arrays.asList(
                context.getResources().getStringArray(R.array.speedUnitsEng)));
    }

    public void setValueAndNotify(String key, String value) {
        if (key != null && !key.isEmpty() && value != null) {
            Object oldValue = mPrefs.get(key);
            if (oldValue == null || !oldValue.toString().equals(value)) {
                mPrefs.put(key, value);
                notifyChanges();
            }
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

    public double getUnit() {
        double value = getDoubleValue(unitKey);
        return value != 0 ? value : 1;
    }

    public List<String> getDistanceUnitSymbol() {
        if (getUnit() == UNITS_SI) {
            return mDistanceUnitsSi;
        }
        return mDistanceUnitsEng;
    }

    public List<String> getSppedUnitSymbol() {
        if (getUnit() == UNITS_SI) {
            return mSpeedUnitsSi;
        }
        return mSpeedUnitsEng;
    }


    private double getDoubleValue(String key) {
        Object value = mPrefs.get(key);
        if (value != null) {
            try {
                return Double.valueOf(value.toString());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return 0;
            }
        } else {
            return 0;
        }
    }

    public List<String> getActions() {
        return mActions;
    }

    public List<String> getMessageTemplateParamTypes() {
        return mMessageTemplateParamTypes;
    }

    public int getMessageTemplateParamTypesCount() {
        return mMessageTemplateParamTypes.size();
    }

    public String getMessageTemplateParamName(int pos) {
        if (pos >= 0 && pos < mMessageTemplateParamTypes.size()) {
            return mMessageTemplateParamTypes.get(pos);
        }
        return "";
    }

    public List<String> getMessageTemplatePreviewValues() {
        return mMessageTemplatePreviewValues;
    }

    public List<String> createMessageTemplateValues(String start, String duration, String distance,
                                                    String speed, String calories, String action,
                                                    String weather, String comment) {
        List<String> list = new ArrayList<>();
        list.add(start);
        list.add(duration);
        list.add(distance);
        list.add(speed);
        list.add(calories);
        list.add(action);
        list.add(weather);
        list.add(comment);
        list.add("[image]");

        return list;
    }

    public String getMessageTemplateDraft() {
        return mMessageTemplateDraft;
    }
}
