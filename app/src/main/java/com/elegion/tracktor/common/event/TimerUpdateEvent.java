package com.elegion.tracktor.common.event;

import com.elegion.tracktor.service.ITrackHelper;

import java.util.Date;

public class TimerUpdateEvent {
    public double distance;
    public long seconds;
    public double averageSpeed;
    public Date startDate;

    public TimerUpdateEvent(ITrackHelper trackHelper) {
        this.distance = trackHelper.getDistance();
        this.seconds = trackHelper.getTotalSecond();
        this.averageSpeed = trackHelper.getAverageSpeed();
        this.startDate = trackHelper.getStartDate();
    }
}
