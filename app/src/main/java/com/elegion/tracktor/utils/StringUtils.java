package com.elegion.tracktor.utils;

import com.elegion.tracktor.BuildConfig;
import com.elegion.tracktor.common.LocationData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StringUtils {
    public static String getDistanceText(Double distance) {
        if (distance == null || distance < 0) {
            distance = 0d;
        }
        if (distance >= 1000) {
            distance = distance / 1000;
            return String.format(Locale.ENGLISH, "%.1f км", distance);
        }
        return String.format(Locale.ENGLISH, "%.1f м", distance);
    }

    public static String getSpeedText(Double speed) {
        if (speed == null || speed < 0) {
            speed = 0d;
        }
        speed = speed * 3.6;
        return String.format(Locale.ENGLISH, "%.1f км/ч", speed);
    }

    public static String getCaloriesText(Double calories) {
        if (calories == null || calories < 0) {
            calories = 0d;
        }
        return String.format(Locale.ENGLISH, "%.1f Ккал", calories);
    }

    public static String getDurationText(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String getLocationDataText(List<LocationData> rawLocationData) {
        if (rawLocationData != null) {
            StringBuilder resultBuilder = new StringBuilder();
            for (LocationData locationData : rawLocationData) {
                resultBuilder.append(locationData.timeSeconds).append(',')
                        .append(locationData.point.latitude).append(',')
                        .append(locationData.point.longitude).append('\n');
            }
            return resultBuilder.toString();
        }
        return "";
    }

    public static String getDateText(Date date) {
        if (date != null) {
            DateFormat dfOut = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            return dfOut.format(date);
        }
        return "";
    }

    public static String getCommentText(String comment) {
        if (comment == null || comment.isEmpty()) {
            return "нет комментария";
        }
        return comment;
    }

    public static String getTemperatureText(double temp) {
        String format = "%.0f º";
        if (temp > 0) {
            format = "+" + format;
        }
        return String.format(Locale.ENGLISH, format, temp);
    }

    public static String getWeatherIconURL(String iconName) {
        if (iconName == null || iconName.isEmpty()) {
            return "";
        }
        return BuildConfig.WEATHER_ICON_URL +
                iconName +
                BuildConfig.WEATHER_ICON_EXTENSION;
    }
}
