package com.elegion.tracktor.common.event;

import com.google.android.gms.maps.model.LatLng;

public class PointFromLocationClientEvent {
    public LatLng location;

    public PointFromLocationClientEvent(LatLng location) {
        this.location = location;
    }
}
