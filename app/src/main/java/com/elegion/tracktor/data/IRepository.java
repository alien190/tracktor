package com.elegion.tracktor.data;

import com.elegion.tracktor.data.model.LocationJobState;

import java.util.Date;
import java.util.List;

public interface IRepository<T> {

    int SORT_ORDER_ASC = 1;
    int SORT_ORDER_DESC = 2;
    int SORT_BY_START_DATE = 1;
    int SORT_BY_DURATION = 2;
    int SORT_BY_DISTANCE = 3;

    long insertTrack(T t);

    T getTrack(long id);

    boolean deleteTrack(long id);

    List<T> getAllTracks();

    List<T> getAllTracks(int sortOrder, int sortBy);

    void updateTrack(T t);

    long createTrackAndSave(long duration, double distance, double averageSpeed, Date startDate,
                            String imageBase64, double temperature, String weatherIconBase64,
                            String weatherDescription);

    void updateLocationJobState(LocationJobState state);


    LocationJobState getLocationJobState();

    void deleteLocationJobState();
}
