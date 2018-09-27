package com.elegion.tracktor.data;

import com.elegion.tracktor.data.model.Track;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmRepository implements IRepository<Track> {

    private AtomicLong currentId = new AtomicLong();
    private Realm mRealm;

    public RealmRepository() {

        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        mRealm = Realm.getInstance(config);
        Number number = mRealm.where(Track.class).max("id");
        if (number != null) {
            currentId.set(number.longValue());
        } else {
            currentId.set(0);
        }

    }

    @Override
    public long insertItem(Track track) {
        track.setId(currentId.incrementAndGet());
        mRealm.beginTransaction();
        mRealm.copyToRealm(track);
        mRealm.commitTransaction();
        return track.getId();
    }

    @Override
    public Track getItem(long id) {
        Track track = getTrackById(id);
        return track != null ? mRealm.copyFromRealm(track) : null;
    }

    @Override
    public boolean deleteItem(long id) {
        boolean isSuccessful = false;
        Track track = getTrackById(id);
        mRealm.beginTransaction();
        if (track != null) {
            track.deleteFromRealm();
            isSuccessful = true;
        }
        mRealm.commitTransaction();
        return isSuccessful;
    }

    @Override
    public List<Track> getAll() {
        return mRealm.copyFromRealm(mRealm.where(Track.class).findAll().sort("id", Sort.ASCENDING));
    }

    @Override
    public void updateItem(Track track) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(track);
        mRealm.commitTransaction();
    }

    private Track getTrackById(long id) {
        return mRealm.where(Track.class).equalTo("id", id).findFirst();
    }

    @Override
    public long createTrackAndSave(long duration, double distance, double averageSpeed,
                                   Date startDate, String imageBase64, double temperature, String weatherIcon) {
        Track track = new Track();

        track.setDuration(duration);
        track.setDistance(distance);
        track.setAverageSpeed(averageSpeed);
        track.setImage(imageBase64);
        track.setDate(startDate);
        track.setTemperature(temperature);
        track.setWeatherIcon(weatherIcon);
        return insertItem(track);
    }

    @Override
    public List<Track> getAll(int sortOrder, int sortBy) {
        RealmResults realmResults = mRealm.where(Track.class).findAll();
        Sort realmSortOrder = sortOrder == IRepository.SORT_ORDER_ASC ? Sort.ASCENDING : Sort.DESCENDING;

        switch (sortBy) {
            case IRepository.SORT_BY_DURATION: {
                realmResults = realmResults.sort("duration", realmSortOrder);
                break;
            }
            case IRepository.SORT_BY_DISTANCE: {
                realmResults = realmResults.sort("distance", realmSortOrder);
                break;
            }
            default: {
                realmResults = realmResults.sort("date", realmSortOrder);
                break;
            }
        }
        return mRealm.copyFromRealm(realmResults);
    }
}
