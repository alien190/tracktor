package com.elegion.tracktor.common.event;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StopRouteEvent implements Parcelable{
    public int routeTime;
    public Double routeDistance;
    public String mRawLocationDataText;
    public List<LatLng> route;

    public StopRouteEvent(List<LatLng> route, int routeTime, Double routeDistance, String rawLocationData) {
        this.route = route;
        this.routeTime = routeTime;
        this.routeDistance = routeDistance;
        this.mRawLocationDataText = rawLocationData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(routeTime);
        parcel.writeDouble(routeDistance);
        parcel.writeString(mRawLocationDataText);
        parcel.writeList(route);
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
    private StopRouteEvent(Parcel parcel){
        routeTime = parcel.readInt();
        routeDistance = parcel.readDouble();
        mRawLocationDataText = parcel.readString();
        route = new ArrayList<>();
        parcel.readList(route, LatLng.class.getClassLoader());
    }


}
