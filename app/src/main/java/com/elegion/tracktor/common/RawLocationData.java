package com.elegion.tracktor.common;

import com.google.android.gms.maps.model.LatLng;

public class RawLocationData {
    public LatLng point;
    public int timeSeconds;

    public RawLocationData(LatLng point, int timeSeconds) {
        this.point = point;
        this.timeSeconds = timeSeconds;
    }
}
