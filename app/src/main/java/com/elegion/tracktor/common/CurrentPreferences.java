package com.elegion.tracktor.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.event.PreferencesChangeEvent;
import com.elegion.tracktor.common.event.TrackDecorationPreferencesChangeEvent;
import com.elegion.tracktor.ui.common.TrackDecoration;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentPreferences {
    private Map mPrefs;
    private Integer[] mKeys = {R.string.sex_key, R.string.weight_key, R.string.height_key, R.string.unit_key, R.string.picture_quality_key, R.string.track_decoration_key};
    private String mWeightKey;
    private String mHeightKey;
    private String mUnitKey;
    private String mPictureQualityKey;
    private String mTrackDecorationKey;
    private List<String> mDistanceUnitsSi;
    private List<String> mDistanceUnitsEng;
    private List<String> mSpeedUnitsSi;
    private List<String> mSpeedUnitsEng;
    private List<String> mActions;
    private List<String> mMessageTemplateParamTypes;
    private List<String> mMessageTemplatePreviewValues;
    private List<Integer> mMarkers;
    private String mMessageTemplateDraft;
    private TrackDecoration mTrackDecoration;
    public static final int UNITS_SI = 1;
    public static final int UNITS_ENG = 2;


    public void notifyChanges() {
        EventBus.getDefault().post(new PreferencesChangeEvent());
    }

    public void notifyChangesTrackDecoration() {
        EventBus.getDefault().post(new TrackDecorationPreferencesChangeEvent());
    }

    public void init(Context context) {
        initPreferences(context);
        initActions(context);
        initKeys(context);
        initMessageTemplate(context);
        initDistanceUnits(context);
        initMarkers(context);
        initTrackDecoration(context);
    }

    private void initTrackDecoration(Context context) {
        mTrackDecoration = new TrackDecoration(getValue(mTrackDecorationKey));
    }

    private void initMarkers(Context context) {
        List<String> resList = new ArrayList<>(Arrays.asList(
                context.getResources().getStringArray(R.array.markerIcons)));
        mMarkers = new ArrayList<>();
        for (String item : resList) {
            mMarkers.add(getResId(item, R.drawable.class));
        }
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
        mWeightKey = context.getString(R.string.weight_key);
        mHeightKey = context.getString(R.string.height_key);
        mUnitKey = context.getString(R.string.unit_key);
        mPictureQualityKey = context.getString(R.string.picture_quality_key);
        mTrackDecorationKey = context.getString(R.string.track_decoration_key);

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
                if (key.equals(mTrackDecorationKey)) {
                    mTrackDecoration.deserialize(value);
                    notifyChangesTrackDecoration();
                } else {
                    notifyChanges();
                }
            }
        }
    }

    public String getValue(String key) {
        Object value = mPrefs.get(key);
        return value != null ? value.toString() : "";
    }

    public double getWeight() {
        double value = getDoubleValue(mWeightKey);
        return value != 0 ? value : 60;
    }

    public double getHeight() {
        double value = getDoubleValue(mHeightKey);
        return value != 0 ? value : 175;
    }

    public double getUnit() {
        double value = getDoubleValue(mUnitKey);
        return value != 0 ? value : 1;
    }

    public List<String> getDistanceUnitSymbol() {
        if (getUnit() == UNITS_SI) {
            return mDistanceUnitsSi;
        }
        return mDistanceUnitsEng;
    }

    public List<String> getSpeedUnitSymbol() {
        if (getUnit() == UNITS_SI) {
            return mSpeedUnitsSi;
        }
        return mSpeedUnitsEng;
    }

    public int getPictureQuality() {
        int ret = getIntegerValue(mPictureQualityKey);
        return ret != 0 ? ret : 100;
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

    private int getIntegerValue(String key) {
        Object value = mPrefs.get(key);
        if (value != null) {
            try {
                return Integer.valueOf(value.toString());
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

    public int getMarkerResId(int pos) {
        if (pos > 0 && pos <= mMarkers.size()) {
            return mMarkers.get(pos - 1);
        }
        return mMarkers.get(0);
    }

    private static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getTrackDecorationColor() {
        return mTrackDecoration.getColor();
    }

    public int getTrackDecorationLineWidth() {
        return mTrackDecoration.getLineWidth();
    }

    public int getTrackDecorationMakrerResId() {
        return getMarkerResId(mTrackDecoration.getMarkerType());
    }
}
