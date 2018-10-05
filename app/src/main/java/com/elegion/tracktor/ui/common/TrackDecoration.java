package com.elegion.tracktor.ui.common;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackDecoration {
    private int mLineWidth;
    private int mColor;
    private int mMarkerType;

    public TrackDecoration(int lineWidth, int color, int markerType) {
        mLineWidth = lineWidth;
        mColor = color;
        mMarkerType = markerType;
    }

    public TrackDecoration() {
        initDefaults();
    }

    public TrackDecoration(String str) {
        deserialize(str);
    }

    private void initDefaults() {
        mLineWidth = 10;
        mColor = Color.GREEN;
        mMarkerType = 1;
    }

    public void deserialize(String str) {
        List<String> list = new ArrayList<>(Arrays.asList(str.split("/")));
        if (list.size() == 3) {
            try {
                mLineWidth = Integer.valueOf(list.get(0));
                mColor = Integer.valueOf(list.get(1));
                mMarkerType = Integer.valueOf(list.get(2));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                initDefaults();
            }
        } else {
            initDefaults();
        }
    }

    public String serialize() {
        String ret;
        ret = String.valueOf(mLineWidth) + "/";
        ret = ret + String.valueOf(mColor) + "/";
        ret = ret + String.valueOf(mMarkerType) + "/";
        return ret;
    }

    public int getLineWidth() {
        return mLineWidth;
    }

    public void setLineWidth(int lineWidth) {
        mLineWidth = lineWidth;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public int getMarkerType() {
        return mMarkerType;
    }

    public void setMarkerType(int markerType) {
        mMarkerType = markerType;
    }
}
