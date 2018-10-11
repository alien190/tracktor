package com.elegion.tracktor.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmLocationData extends RealmObject {
    @PrimaryKey
    private long mTimeSeconds;
    private double mLat;
    private double mLng;


    public RealmLocationData() {
    }

    public RealmLocationData(long timeSeconds, double lat, double lng) {
        mTimeSeconds = timeSeconds;
        mLat = lat;
        mLng = lng;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLng() {
        return mLng;
    }

    public void setLng(double lng) {
        mLng = lng;
    }

    public long getTimeSeconds() {
        return mTimeSeconds;
    }

    public void setTimeSeconds(long timeSeconds) {
        mTimeSeconds = timeSeconds;
    }

}
