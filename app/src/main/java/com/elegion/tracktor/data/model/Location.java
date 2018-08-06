package com.elegion.tracktor.data.model;


import io.realm.RealmObject;

public class Location extends RealmObject {
    private double mLatitude;
    private double mLongitude;


    public Location() {
    }

    public Location(double mLatitude, double mLongitude) {
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }
}
