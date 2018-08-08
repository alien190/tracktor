package com.elegion.tracktor.data.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Track extends RealmObject {

    @PrimaryKey
    private long id;
    private Date date;
    private long duration;
    private double distance;
    private String image;
    private RealmList<Location> route;


    public long getId() {
        return id;
    }

    public void setId(long mId) {
        this.id = mId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date mDate) {
        this.date = mDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long mDuration) {
        this.duration = mDuration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double mDistance) {
        this.distance = mDistance;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String mImage) {
        this.image = mImage;
    }

    public RealmList<Location> getRoute() {
        return route;
    }

    public void setRoute(RealmList<Location> mRoute) {
        this.route = mRoute;
    }



}
