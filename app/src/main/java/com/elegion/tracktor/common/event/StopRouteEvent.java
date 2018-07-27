package com.elegion.tracktor.common.event;


import java.io.Serializable;

public class StopRouteEvent implements Serializable{
    public String routeTime;
    public Double routeDistance;
    public String mRawLocationDataText;

    public StopRouteEvent(String routeTime, Double routeDistance, String rawLocationData) {
        this.routeTime = routeTime;
        this.routeDistance = routeDistance;
        this.mRawLocationDataText = rawLocationData;
    }
}
