package com.elegion.tracktor.common.event;

import java.util.List;

public class RequestRouteUpdateEvent {
    public List<Integer> times;

    public RequestRouteUpdateEvent(List<Integer> times) {
        this.times = times;
    }
}
