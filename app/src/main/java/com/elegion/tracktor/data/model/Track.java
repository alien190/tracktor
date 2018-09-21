package com.elegion.tracktor.data.model;

import com.elegion.tracktor.utils.StringUtils;

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
    private double averageSpeed;
    private int action;
    private String comment;
    private double calories;


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

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Track) {
            Track newTrack = (Track) obj;
            return this.getDistance() == newTrack.getDistance() &&
                    this.getDuration() == newTrack.getDuration() &&
                    this.getDate() == newTrack.getDate() &&
                    this.getAverageSpeed() == newTrack.getAverageSpeed();
        }
        return false;
    }
}
