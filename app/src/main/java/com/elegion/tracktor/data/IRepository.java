package com.elegion.tracktor.data;

import java.util.Date;
import java.util.List;

public interface IRepository<T> {

    int SORT_ORDER_ASC = 1;
    int SORT_ORDER_DESC = 2;
    int SORT_BY_START_DATE = 1;
    int SORT_BY_DURATION = 2;
    int SORT_BY_DISTANCE = 3;

    long insertItem(T t);

    T getItem(long id);

    boolean deleteItem(long id);

    List<T> getAll();

    List<T> getAll(int sortOrder, int sortBy);

    void updateItem(T t);

    long createTrackAndSave(long duration, double distance, double averageSpeed, Date startDate,
                            String imageBase64, double temperature, String weatherIconBase64);
}
