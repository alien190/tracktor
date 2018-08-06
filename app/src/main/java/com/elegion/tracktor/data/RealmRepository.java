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
        mRealm.insert(track);
        return track.getId();
    }

    @Override
    public Track getItem(long id) {
        return mRealm.where(Track.class).equalTo("Id", id).findFirst();
    }

    @Override
    public boolean deleteItem(long id) {
        Track track = mRealm.where(Track.class).equalTo("Id", id).findFirst();
        if (track != null) {
            track.deleteFromRealm();
            return true;
        }
        return false;
    }

    @Override
    public List<Track> getAll() {
        RealmResults<Track> tracks = mRealm.where(Track.class).findAll();
        return mRealm.copyFromRealm(tracks);
    }

    @Override
    public void updateItem(Track track) {
        mRealm.insertOrUpdate(track);
    }
}
