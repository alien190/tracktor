package com.elegion.tracktor.service;

import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public interface ITrackHelper {
    void start();
    SegmentForRouteEvent onRouteUpdate(LocationData newPoint);
    SegmentForRouteEvent getLastSegment();
    List<LatLng> getRoute();
    double getDistance();
    long getTotalSecond();
    Date getStartDate();
    double getAverageSpeed();
}
