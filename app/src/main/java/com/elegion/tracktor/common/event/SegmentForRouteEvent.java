package com.elegion.tracktor.common.event;

import android.util.Pair;

import com.elegion.tracktor.common.LocationData;
import com.google.maps.android.SphericalUtil;

public class SegmentForRouteEvent {
    public Pair<LocationData, LocationData> points;

    public SegmentForRouteEvent(Pair<LocationData, LocationData> locationDataPair) {
        this.points = locationDataPair;
    }

    public double getSegmentDistance() {
        return SphericalUtil.computeDistanceBetween(points.first.point, points.second.point);
    }
}
