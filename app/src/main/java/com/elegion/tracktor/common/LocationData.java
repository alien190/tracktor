package com.elegion.tracktor.common;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

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

}
