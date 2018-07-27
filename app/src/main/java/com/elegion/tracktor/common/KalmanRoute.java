package com.elegion.tracktor.common;

import android.util.Pair;

import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.utils.StringUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

public class KalmanRoute {
    private static String TAG = "KalmanRouteTAG";
    private static double ROUTE_ACCURACY_IN_METERS = 10;
    private double mKoeff;
    private List<LocationData> mRoutePoints = new ArrayList<>();
    private LocationData lastRawPoint;

    public KalmanRoute(double koeff) {
        mKoeff = koeff;
    }

    public KalmanRoute() {
        mKoeff = 0.1;
    }

    public void onRouteUpdate(LocationData newPoint) {

        lastRawPoint = newPoint;

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

    public SegmentForRouteEvent getLastSegment() {
        int size = mRoutePoints.size();
        if (size > 1 && SphericalUtil.computeDistanceBetween(mRoutePoints.get(size - 1).point,
                lastRawPoint.point) > ROUTE_ACCURACY_IN_METERS) {
            return new SegmentForRouteEvent(new Pair<>(mRoutePoints.get(size - 2),
                    mRoutePoints.get(size - 1)));
        } else return null;
    }


}
