package com.elegion.tracktor.common;

import com.elegion.tracktor.utils.StringUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class KalmanRoute {
    private static String TAG = "KalmanRouteTAG";
    private double mKoeff;
    private List<LocationData> mRoutePoints = new ArrayList<>();

    public KalmanRoute(double koeff) {
        mKoeff = koeff;
    }

    public KalmanRoute() {
        mKoeff = 0.1;
    }

    public void onRouteUpdate(LocationData newPoint) {
        if (mRoutePoints.size() == 0) {
            mRoutePoints.add(newPoint);
        } else {
            LocationData lastPoint = mRoutePoints.get(mRoutePoints.size() - 1);

            double newPointLat = mKoeff * newPoint.point.latitude
                    + (1 - mKoeff) * lastPoint.point.latitude;

            double newPointLng = mKoeff * newPoint.point.longitude
                    + (1 - mKoeff) * lastPoint.point.longitude;

            mRoutePoints.add(new LocationData(new LatLng(newPointLat, newPointLng),
                    newPoint.timeSeconds));
        }
    }

    @Override
    public String toString() {
        return StringUtils.getLocationDataText(mRoutePoints);
    }
}
