package com.elegion.tracktor.common.event;

import com.elegion.tracktor.common.LocationData;

public class StartRouteEvent {
    public LocationData firstPoint;

    public StartRouteEvent(LocationData firstPoint) {
        this.firstPoint = firstPoint;
    }
}
