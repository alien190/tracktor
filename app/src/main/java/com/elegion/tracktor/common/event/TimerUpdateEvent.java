package com.elegion.tracktor.common.event;

import java.util.Date;

public class TimerUpdateEvent {
    public double distance;
    public int seconds;
    public double averageSpeed;
    public Date startDate;

    public TimerUpdateEvent(double distance, int seconds, double averageSpeed, Date startDate) {
        this.distance = distance;
        this.seconds = seconds;
        this.averageSpeed = averageSpeed;
        this.startDate = startDate;
    }
}
