package com.elegion.tracktor.common;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class RawLocationData implements Serializable {
    public LatLng point;
    public int timeSeconds;

    public RawLocationData(LatLng point, int timeSeconds) {
        this.point = point;
        this.timeSeconds = timeSeconds;
    }
}
