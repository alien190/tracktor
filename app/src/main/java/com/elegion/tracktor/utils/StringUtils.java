package com.elegion.tracktor.utils;

import com.elegion.tracktor.common.LocationData;

import java.util.List;
import java.util.Locale;

public class StringUtils {
    public static String getDistanceText(Double distance) {
        return String.format(Locale.ENGLISH, "%.1f m", distance);
    }

    public static String getTimerText(int totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getLocationDataText(List<LocationData> rawLocationData) {
        StringBuilder resultBuilder = new StringBuilder();
        for (LocationData locationData:rawLocationData) {
            resultBuilder.append(locationData.timeSeconds).append(',')
                    .append(locationData.point.latitude).append(',')
                    .append(locationData.point.longitude).append('\n');
        }

        return resultBuilder.toString();
    }
}
