package com.elegion.tracktor.ui.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.elegion.tracktor.R;

public class TrackLinePreference extends DialogPreference {

    private String mValue;

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

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
        persistString(value);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedString(mValue) : (String) defaultValue);
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.fr_track_decoration_dialog;
    }

}
