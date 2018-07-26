package com.elegion.tracktor.event;

import java.io.Serializable;

public class StopRouteEvent implements Serializable{
    public String routeTime;
    public Double routeDistance;

    public StopRouteEvent(String routeTime, Double routeDistance) {
        this.routeTime = routeTime;
        this.routeDistance = routeDistance;
    }
}
