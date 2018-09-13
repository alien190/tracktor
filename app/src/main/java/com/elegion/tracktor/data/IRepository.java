package com.elegion.tracktor.data;

import java.util.List;

public interface IRepository<T> {

    long insertItem(T t);

    T getItem(long id);

    boolean deleteItem(long id);

    List<T> getAll();

    void updateItem(T t);

    long createTrackAndSave(long duration, double distance, String imageBase64);
}
