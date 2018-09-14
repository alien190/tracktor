package com.elegion.tracktor.data;

import com.elegion.tracktor.data.model.Track;

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
    public long createTrackAndSave(long duration, double distance, String imageBase64) {

        Track track = new Track();

        track.setDuration(duration);
        track.setDistance(distance);
        track.setImage(imageBase64);
        return insertItem(track);
    }
}
