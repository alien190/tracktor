package com.elegion.tracktor.data;

import com.elegion.tracktor.data.model.LocationJobState;
import com.elegion.tracktor.data.model.Track;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmRepository implements IRepository<Track> {

    private AtomicLong currentTrackId = new AtomicLong();
    private Realm mRealm;
    private RealmConfiguration mRealmConfiguration;

    public RealmRepository() {
        mRealmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        mRealm = Realm.getInstance(mRealmConfiguration);
        currentTrackId.set(getInitialId(Track.class));
    }

    private long getInitialId(Class itemClass) {
        Number number = mRealm.where(itemClass).max("id");
        if (number != null) {
            return number.longValue();
        } else {
            return 0;
        }
    }

    @Override
    public long insertTrack(Track track) {
        track.setId(currentTrackId.incrementAndGet());
        mRealm.beginTransaction();
        mRealm.copyToRealm(track);
        mRealm.commitTransaction();
        return track.getId();
    }

    @Override
    public void updateLocationJobState(LocationJobState state) {
        Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(state);
        realm.commitTransaction();
    }

    @Override
    public LocationJobState getLocationJobState() {
        Realm realm = Realm.getInstance(mRealmConfiguration);
        LocationJobState state = realm.where(LocationJobState.class).findFirst();
        return state != null ? realm.copyFromRealm(state) : null;

    }

    @Override
    public void deleteLocationJobState() {
        Realm realm = Realm.getInstance(mRealmConfiguration);
        realm.where(LocationJobState.class).findAll().deleteAllFromRealm();
    }

    @Override
    public Track getTrack(long id) {
        Track track = getTrackById(id);
        return track != null ? mRealm.copyFromRealm(track) : null;
    }

    @Override
    public boolean deleteTrack(long id) {
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
    public List<Track> getAllTracks() {
        return mRealm.copyFromRealm(mRealm.where(Track.class).findAll().sort("id", Sort.ASCENDING));
    }

    @Override
    public void updateTrack(Track track) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(track);
        mRealm.commitTransaction();
    }

    private Track getTrackById(long id) {
        return mRealm.where(Track.class).equalTo("id", id).findFirst();
    }

    @Override
    public long createTrackAndSave(long duration, double distance, double averageSpeed,
                                   Date startDate, String imageBase64, double temperature,
                                   String weatherIcon, String weatherDescription) {
        Track track = new Track();

        track.setDuration(duration);
        track.setDistance(distance);
        track.setAverageSpeed(averageSpeed);
        track.setImage(imageBase64);
        track.setDate(startDate);
        track.setTemperature(temperature);
        track.setWeatherIcon(weatherIcon);
        track.setWeatherDescription(weatherDescription);
        return insertTrack(track);
    }

    @Override
    public List<Track> getAllTracks(int sortOrder, int sortBy) {
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
