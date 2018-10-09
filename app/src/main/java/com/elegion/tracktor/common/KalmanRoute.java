package com.elegion.tracktor.common;

import android.util.Pair;

import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.service.ITrackHelper;
import com.elegion.tracktor.utils.StringUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class KalmanRoute implements ITrackHelper {
    private static String TAG = "KalmanRouteTAG";
    private static double ROUTE_ACCURACY_METERS = 10;
    private double mKoeff;
    private List<LocationData> mRoutePoints = new ArrayList<>();
    private LocationData lastRawPoint;
    private LocationData lastPointForSegment;
    private double mDistance;
    private long mTotalSecond;
    private Date mStartDate;
    private double mAverageSpeed;


    public KalmanRoute(double koeff) {
        mKoeff = koeff;
    }

    public KalmanRoute() {
        mKoeff = 1;
    }

    private void init() {
        mDistance = 0;
        mTotalSecond = 0;
        mAverageSpeed = 0;
    }

    @Override
    public void start() {
        init();
        mStartDate = Calendar.getInstance().getTime();
    }

    public SegmentForRouteEvent onRouteUpdate(LocationData newPoint) {
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
        return updateMetrics();
    }

    private SegmentForRouteEvent updateMetrics() {
        mTotalSecond = (Calendar.getInstance().getTimeInMillis() - mStartDate.getTime()) / 1000;
        SegmentForRouteEvent newSegment = getLastSegment();
        if (newSegment != null) {
            mDistance += newSegment.getSegmentDistance();
        }
        mAverageSpeed = mDistance / (mTotalSecond == 0 ? 1 : mTotalSecond);
        return newSegment;
    }

    @Override
    public String toString() {
        return StringUtils.getLocationDataText(mRoutePoints);
    }

    public SegmentForRouteEvent getLastSegment() {
        int size = mRoutePoints.size();
        if (size > 1) {
            if (lastPointForSegment == null) {
                lastPointForSegment = mRoutePoints.get(size - 2);
            }

            if (SphericalUtil.computeDistanceBetween(lastPointForSegment.point,
                    lastRawPoint.point) > ROUTE_ACCURACY_METERS) {
                SegmentForRouteEvent segmentForRouteEvent = new SegmentForRouteEvent(
                        new Pair<>(lastPointForSegment, mRoutePoints.get(size - 1)));
                lastPointForSegment = mRoutePoints.get(size - 1);
                return segmentForRouteEvent;
            }
        }
        return null;
    }

    public List<LatLng> getRoute() {
        List<LatLng> route = new ArrayList<>();

        for (LocationData locationData : mRoutePoints) {
            route.add(locationData.point);
        }

        return route;
    }

    public double getDistance() {
        return mDistance;
    }

    public long getTotalSecond() {
        return mTotalSecond;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public double getAverageSpeed() {
        return mAverageSpeed;
    }
}
