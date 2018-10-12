package com.elegion.tracktor.service;

import com.elegion.tracktor.data.IRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public interface ITrackHelper {
    void start();
    void resume();
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
    boolean loadFromRealm(IRepository repository);
    void saveToRealm(IRepository repository);
}
