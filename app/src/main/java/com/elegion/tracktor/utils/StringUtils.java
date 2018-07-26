package com.elegion.tracktor.utils;

import java.util.Locale;

public class StringUtils {
    public static String convertDistance(Double distance) {
        return String.format(Locale.ENGLISH, "%.1f m", distance);
    }
}
