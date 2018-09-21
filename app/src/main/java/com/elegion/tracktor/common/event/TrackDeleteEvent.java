package com.elegion.tracktor.common.event;

public class TrackDeleteEvent {
    public long trackId;

    public TrackDeleteEvent(long trackId) {
        this.trackId = trackId;
    }
}
