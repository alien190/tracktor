package com.elegion.tracktor.data;

import com.elegion.tracktor.data.model.Track;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmRepository implements IRepository<Track> {

    private AtomicLong currentId = new AtomicLong();
    private Realm mRealm;

    public RealmRepository() {
        mRealm = Realm.getDefaultInstance();
        Number number = mRealm.where(Track.class).max("id");
        if (number != null) {
            currentId.set(number.longValue());
        } else {
            currentId.set(0);
        }

    }

    @Override
    public long insertItem(Track track) {
        track.setId(currentId.getAndIncrement());
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
        RealmResults<Track> tracks = mRealm.where(Track.class).findAll();
        return tracks != null ? mRealm.copyFromRealm(tracks) : null;
    }

    @Override
    public void updateItem(Track track) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(track);
        mRealm.commitTransaction();
    }

    private Track getTrackById(long id) {
        return mRealm.where(Track.class).equalTo("Id", id).findFirst();
    }
}
