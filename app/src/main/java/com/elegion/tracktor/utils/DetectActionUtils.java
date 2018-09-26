package com.elegion.tracktor.utils;

import com.elegion.tracktor.R;

public class DetectActionUtils {
    public static int getDetectActionIconId(double speed) {
        speed = speed * 3.6;
        if(speed < 2) {
            return R.drawable.ic_turtle_black_24dp;
        } else if(speed <= 4) {
            return R.drawable.ic_man_walking_black_24dp;
        }else if(speed <= 9) {
            return R.drawable.ic_man_running_black_24dp;
        } else if(speed <= 15) {
            return R.drawable.ic_bicycle_black_24dp;
        } else {
            return R.drawable.ic_car_black_24dp;
        }
    }
}
