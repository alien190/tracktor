package com.elegion.tracktor.utils;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.data.model.Track;

public class CommonUtils {
    public static int detectActionIconId(double speed) {
        speed = speed * 3.6;
        if (speed < 2) {
            return R.drawable.ic_turtle_black_24dp;
        } else if (speed <= 4) {
            return R.drawable.ic_man_walking_black_24dp;
        } else if (speed <= 9) {
            return R.drawable.ic_man_running_black_24dp;
        } else if (speed <= 15) {
            return R.drawable.ic_bicycle_black_24dp;
        } else {
            return R.drawable.ic_car_black_24dp;
        }
    }

    public static double calculateCalories(Track track, CurrentPreferences currentPreferences) {
        double calories;

        switch (track.getAction()) {
            case 1: { //ходьба
                calories = 0.035 * currentPreferences.getWeight()
                        + Math.pow(track.getAverageSpeed(), 2) /
                        currentPreferences.getHeight() * 2.9 *
                        currentPreferences.getWeight();
                break;
            }
            case 2: { //бег
                calories = currentPreferences.getWeight() * track.getDistance() / 1000;
                break;
            }
            case 3: { //велосипед
                calories = currentPreferences.getWeight() / 70 * 300 * track.getDuration() / 3600;
                break;
            }
            default: { //автомобиль и др.
                calories = currentPreferences.getWeight() / 70 * 70 * track.getDuration() / 3600;
                break;
            }
        }
        return calories;
    }
}
