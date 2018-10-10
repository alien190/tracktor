package com.elegion.tracktor.common.event;

import com.elegion.tracktor.service.ITrackHelper;

import java.util.Date;

public class TimerUpdateEvent {
    public double distance;
    public long seconds;
    public double averageSpeed;
    public Date startDate;
    public Double temperature;
    public String weatherIcon;
    public String weatherDescription;

    public TimerUpdateEvent(ITrackHelper trackHelper) {
        this.distance = trackHelper.getDistance();
        this.seconds = trackHelper.getTotalSecond();
        this.averageSpeed = trackHelper.getAverageSpeed();
        this.startDate = trackHelper.getStartDate();
        this.temperature = trackHelper.getTemperature();
        this.weatherIcon = trackHelper.getWeatherIcon();
        this.weatherDescription = trackHelper.getWeatherDescription();
    }
}
