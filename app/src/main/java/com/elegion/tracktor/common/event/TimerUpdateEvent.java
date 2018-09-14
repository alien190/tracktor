package com.elegion.tracktor.common.event;

public class TimerUpdateEvent {
    public double distance;
    public int seconds;
    public double averageSpeed;

    public TimerUpdateEvent(double distance, int seconds, double averageSpeed) {
        this.distance = distance;
        this.seconds = seconds;
        this.averageSpeed = averageSpeed;
    }
}
