package com.elegion.tracktor.common.event;

import android.support.v4.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class RouteUpdateEvent {
    //List<Pair<LatLng,LatLng>> points;
    public List<LatLng> points;

//    public RouteUpdateEvent(List<Pair<LatLng, LatLng>> points) {
//        this.points = points;
//    }

    public RouteUpdateEvent(List<LatLng> points) {
        this.points = points;
    }
}
