package com.elegion.tracktor.utils;

import com.elegion.tracktor.common.CurrentPreferences;

import java.util.List;
import java.util.Locale;

public class DistanceConverter {
    private CurrentPreferences mCurrentPreferences;

    public DistanceConverter(CurrentPreferences currentPreferences) {
        mCurrentPreferences = currentPreferences;
    }

    public String convertDistance(Double distanceMeters) {
        try {
            double distance;
            String format = "%.1f ";
            List<String> unitSymbols = mCurrentPreferences.getDistanceUnitSymbol();

            if (distanceMeters == null || distanceMeters < 0) {
                distance = 0d;
            } else {
                distance = distanceMeters;
            }

            if (mCurrentPreferences.getUnit() == CurrentPreferences.UNITS_SI) {
                if (distance >= 1000) {
                    distance = distance / 1000;
                    format = format + unitSymbols.get(1);
                } else {
                    format = format + unitSymbols.get(0);
                }

            } else {
                distance = distance / 0.3048;
                if (distance >= 5280) {
                    distance = distance / 5280;
                    format = format + unitSymbols.get(1);
                } else {
                    format = format + unitSymbols.get(0);
                }
            }
            return String.format(Locale.ENGLISH, format, distance);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return "";
        }
    }


    public String convertSpeed(Double sppedMetersPerSecs) {
        try {
            double speed;
            String format = "%.1f " + mCurrentPreferences.getSppedUnitSymbol().get(0);

            if (sppedMetersPerSecs == null || sppedMetersPerSecs < 0) {
                speed = 0d;
            } else {
                speed = sppedMetersPerSecs;
            }
            if (mCurrentPreferences.getUnit() == CurrentPreferences.UNITS_SI) {
                speed = speed * 3.6;
            } else {
                speed = speed * 2.2369;
            }
            return String.format(Locale.ENGLISH, format, speed);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return "";
        }
    }
}
