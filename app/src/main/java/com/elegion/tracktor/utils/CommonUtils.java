package com.elegion.tracktor.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.data.model.LocationJobState;
import com.elegion.tracktor.data.model.RealmLocationData;
import com.elegion.tracktor.data.model.Track;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

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


    public static boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }

            }
        }
        return false;
    }

    public static RealmLocationData locationDataToRealm(LocationData data) {
        if (data != null) {
            return new RealmLocationData(data.timeSeconds,
                    data.point.latitude,
                    data.point.longitude);
        } else {
            return null;
        }
    }

    public static LocationData realmToLocationData(RealmLocationData data) {
        if (data != null) {
            return new LocationData(new LatLng(data.getLat(), data.getLng()), data.getTimeSeconds());
        } else {
            return null;
        }
    }

    public static List<LatLng> locationJobStatePointsToLatLngList(LocationJobState state) {
        if (state != null && state.getRoutePoints() != null) {
            List<LatLng> list = new ArrayList<>();
            for (RealmLocationData data : state.getRoutePoints()) {
                list.add(CommonUtils.realmToLocationData(data).point);
            }
            return list;
        }
        return null;
    }
}