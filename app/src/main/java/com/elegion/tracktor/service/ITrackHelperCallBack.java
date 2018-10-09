package com.elegion.tracktor.service;

import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;

public interface ITrackHelperCallBack {
    void onMetricsUpdate();
    void onFirstPoint(LocationData point);
    void onRouteUpdate(SegmentForRouteEvent segment);
}
