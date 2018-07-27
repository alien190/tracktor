package com.elegion.tracktor.common.event;

import com.elegion.tracktor.common.RawLocationData;

import java.io.Serializable;
import java.util.List;

public class StopRouteEvent implements Serializable{
    public String routeTime;
    public Double routeDistance;
    public List<RawLocationData> mRawLocationDataList;

    public StopRouteEvent(String routeTime, Double routeDistance, List<RawLocationData> rawLocationData) {
        this.routeTime = routeTime;
        this.routeDistance = routeDistance;
        this.mRawLocationDataList = rawLocationData;
    }
}
