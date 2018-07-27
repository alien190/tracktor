package com.elegion.tracktor.common.event;

import com.google.android.gms.maps.model.LatLng;

public class NewPointFromLocationClientEvent {
    public LatLng location;

    public NewPointFromLocationClientEvent(LatLng location) {
        this.location = location;
    }
}
