package com.elegion.tracktor.common.event;


import android.os.Parcel;
import android.os.Parcelable;

import com.elegion.tracktor.data.model.LocationJobState;
import com.elegion.tracktor.data.model.RealmLocationData;
import com.elegion.tracktor.service.ITrackHelper;
import com.elegion.tracktor.utils.CommonUtils;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StopRouteEvent implements Parcelable {
    public long routeTime;
    public Double routeDistance;
    public String mRawLocationDataText;
    public List<LatLng> route;
    public double averageSpeed;
    public Date startDate;
    public Double temperature;
    public String weatherIcon;
    public String weatherDescription;

    public StopRouteEvent(ITrackHelper trackHelper) {
        this.route = trackHelper.getRoute();
        this.routeTime = trackHelper.getTotalSecond();
        this.routeDistance = trackHelper.getDistance();
        this.averageSpeed = trackHelper.getAverageSpeed();
        this.startDate = trackHelper.getStartDate();
        this.temperature = trackHelper.getTemperature();
        this.weatherIcon = trackHelper.getWeatherIcon();
        this.weatherDescription = trackHelper.getWeatherDescription();
    }

    public StopRouteEvent(LocationJobState state) {
        if (state != null) {
            this.route = CommonUtils.locationJobStatePointsToLatLngList(state);
            this.routeTime = state.getTotalSecond();
            this.routeDistance = state.getDistance();
            this.averageSpeed = state.getAverageSpeed();
            this.startDate = state.getStartDate();
            this.temperature = state.getTemperature();
            this.weatherIcon = state.getWeatherIcon();
            this.weatherDescription = state.getWeatherDescription();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(routeTime);
        parcel.writeDouble(routeDistance);
        parcel.writeString(mRawLocationDataText);
        parcel.writeList(route);
        parcel.writeDouble(averageSpeed);
        parcel.writeLong(startDate.getTime());
        parcel.writeDouble(temperature);
        parcel.writeString(weatherIcon);
        parcel.writeString(weatherDescription);
    }

    public static final Parcelable.Creator<StopRouteEvent> CREATOR
            = new Parcelable.Creator<StopRouteEvent>() {
        @Override
        public StopRouteEvent createFromParcel(Parcel parcel) {
            return new StopRouteEvent(parcel);
        }

        @Override
        public StopRouteEvent[] newArray(int i) {
            return new StopRouteEvent[i];
        }
    };

    private StopRouteEvent(Parcel parcel) {
        routeTime = parcel.readLong();
        routeDistance = parcel.readDouble();
        mRawLocationDataText = parcel.readString();
        route = new ArrayList<>();
        parcel.readList(route, LatLng.class.getClassLoader());
        averageSpeed = parcel.readDouble();
        startDate.setTime(parcel.readLong());
        temperature = parcel.readDouble();
        weatherIcon = parcel.readString();
        weatherDescription = parcel.readString();
    }


}
