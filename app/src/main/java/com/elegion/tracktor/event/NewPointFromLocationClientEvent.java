package com.elegion.tracktor.event;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class NewPointFromLocationClientEvent {
    public LatLng location;

    public NewPointFromLocationClientEvent(LatLng location) {
        this.location = location;
    }
}
