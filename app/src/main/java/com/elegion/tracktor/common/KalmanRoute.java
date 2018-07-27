package com.elegion.tracktor.common;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class KalmanRoute {
    private static String TAG = "KalmanRouteTAG";
    private LatLng mLastRawPoint;

    public void onRouteUpdate(LatLng newPoint) {
        if (mLastRawPoint == null || !mLastRawPoint.equals(newPoint))
        {
            mLastRawPoint = newPoint;
            Log.d(TAG, "onRouteUpdate: " + newPoint.toString());
        }
        else {
            Log.d(TAG, "onRouteUpdate: same point");
        }
    }
}
