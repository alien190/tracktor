package com.elegion.tracktor.event;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class AddPointToRouteEvent {
    public LatLng location;

    public AddPointToRouteEvent(LatLng location) {
        this.location = location;
    }
}
