package com.elegion.tracktor.ui.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.elegion.tracktor.R;

public class TrackLinePreference extends DialogPreference {

    private int mValue;

    public TrackLinePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TrackLinePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TrackLinePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrackLinePreference(Context context) {
        super(context);
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int value) {
        mValue = value;
        persistInt(value);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(mValue) : (int) defaultValue);
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.dialog_track_line;
    }

}
