package com.elegion.tracktor.service;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public interface ITrackHelper {
    void start();
    void stop();
    void onRouteUpdate(LatLng newPoint);
    List<LatLng> getRoute();
    double getDistance();
    long getTotalSecond();
    Date getStartDate();
    double getAverageSpeed();
    void setCallBack(ITrackHelperCallBack callBack);
    Double getTemperature();
    String getWeatherDescription();
    String getWeatherIcon();
}
