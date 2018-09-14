package com.elegion.tracktor.data;

import java.util.Date;
import java.util.List;

public interface IRepository<T> {

    long insertItem(T t);

    T getItem(long id);

    boolean deleteItem(long id);

    List<T> getAll();

    void updateItem(T t);

    long createTrackAndSave(long duration, double distance, double averageSpeed, Date startDate, String imageBase64);
}
