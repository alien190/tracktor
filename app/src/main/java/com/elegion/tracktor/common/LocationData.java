package com.elegion.tracktor.common;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class LocationData implements Serializable {
    public LatLng point;
    public long timeSeconds;


    public LocationData(LatLng point, long  timeSeconds) {
        this.point = point;
        this.timeSeconds = timeSeconds;
    }

    public LocationData(LocationData locationData, long timeSeconds) {
        this.point = locationData.point;
        this.timeSeconds = timeSeconds;
    }

    public LatLng getPoint() {
        return point;
    }

    public void setPoint(LatLng point) {
        this.point = point;
    }

    public long getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(long timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

}
