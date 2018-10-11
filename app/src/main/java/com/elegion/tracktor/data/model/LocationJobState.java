package com.elegion.tracktor.data.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocationJobState extends RealmObject {
    @PrimaryKey
    private long mId = 1;
    private double mKoeff;
    private RealmList<RealmLocationData> mRoutePoints;
    private RealmLocationData mLastRawPoint;
    private RealmLocationData mLastPointForSegment;
    private double mDistance;
    private long mTotalSecond;
    private Date mStartDate;
    private double mAverageSpeed;
    private boolean isStarted;

    public LocationJobState() {
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public double getKoeff() {
        return mKoeff;
    }

    public void setKoeff(double koeff) {
        mKoeff = koeff;
    }

    public RealmList<RealmLocationData> getRoutePoints() {
        return mRoutePoints;
    }

    public void setRoutePoints(RealmList<RealmLocationData> routePoints) {
        mRoutePoints = routePoints;
    }

    public RealmLocationData getLastRawPoint() {
        return mLastRawPoint;
    }

    public void setLastRawPoint(RealmLocationData lastRawPoint) {
        mLastRawPoint = lastRawPoint;
    }

    public RealmLocationData getLastPointForSegment() {
        return mLastPointForSegment;
    }

    public void setLastPointForSegment(RealmLocationData lastPointForSegment) {
        mLastPointForSegment = lastPointForSegment;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        mDistance = distance;
    }

    public long getTotalSecond() {
        return mTotalSecond;
    }

    public void setTotalSecond(long totalSecond) {
        mTotalSecond = totalSecond;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public double getAverageSpeed() {
        return mAverageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        mAverageSpeed = averageSpeed;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }


}
