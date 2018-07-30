package com.elegion.tracktor.common;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class LocationData implements Serializable {
    public LatLng point;
    public int timeSeconds;

    public LocationData(LatLng point, int timeSeconds) {
        this.point = point;
        this.timeSeconds = timeSeconds;
    }

    public LocationData(LocationData locationData, int timeSeconds) {
        this.point = locationData.point;
        this.timeSeconds = timeSeconds;
    }

}
