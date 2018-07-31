package com.elegion.tracktor.common.event;

public class TimerUpdateEvent {
    public double distance;
    public int seconds;

    public TimerUpdateEvent(double distance, int seconds) {
        this.distance = distance;
        this.seconds = seconds;
    }
}
