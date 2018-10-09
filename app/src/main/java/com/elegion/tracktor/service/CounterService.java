package com.elegion.tracktor.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.KalmanRoute;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.NotificationHelper;
import com.elegion.tracktor.common.event.RequestRouteUpdateEvent;
import com.elegion.tracktor.common.event.RouteUpdateEvent;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.common.event.ShutdownEvent;
import com.elegion.tracktor.common.event.StartRouteEvent;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.common.event.TimerUpdateEvent;
import com.elegion.tracktor.utils.IDistanceConverter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import toothpick.Scope;
import toothpick.Toothpick;

public class CounterService extends Service {

    public static final int UPDATE_INTERVAL = 5000;
    public static final int UPDATE_FASTEST_INTERVAL = 2000;
    public static final int UPDATE_MIN_DISTANCE = 10;
    private List<LocationData> mRawLocationData = new ArrayList<>();

    private ITrackHelper mTrackHelper;
    private Disposable timerDisposable;

    private boolean isStartPointSend;

    private Long mShutdownInterval = -1L;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest = new LocationRequest();

    @Inject
    protected IDistanceConverter mDistanceConverter;

    private INotificationHelper mNotificationHelper;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mRawLocationData.add(new LocationData(newPosition, mTrackHelper.getTotalSecond()));
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Scope scope = Toothpick.openScope("Application");
        Toothpick.inject(this, scope);

        mTrackHelper = new KalmanRoute();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mShutdownInterval = Long.valueOf(sharedPreferences.getString(getString(R.string.shutdown_key), "-1"));

        mNotificationHelper = new NotificationHelper(this, mDistanceConverter);
        EventBus.getDefault().register(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();

        mTrackHelper.start();

        timerDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(seconds -> onTimerUpdate(seconds.intValue()));

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(UPDATE_FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(UPDATE_MIN_DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return START_REDELIVER_INTENT;
    }

    public void startForeground() {
        startForeground(NotificationHelper.DEFAULT_NOTIFICATION_ID,
                mNotificationHelper.getNotification(getApplicationContext()));
    }

    private void onTimerUpdate(int totalSeconds) {

        if (!isStartPointSend && mRawLocationData.size() != 0) {
            EventBus.getDefault().post(new StartRouteEvent(mRawLocationData.get(0)));
            isStartPointSend = true;
        }

        if (mRawLocationData.size() != 0) {
            SegmentForRouteEvent newSegment = mTrackHelper.onRouteUpdate(
                    new LocationData(mRawLocationData.get(mRawLocationData.size() - 1),
                            totalSeconds));
            if (newSegment != null) {
                EventBus.getDefault().post(new SegmentForRouteEvent(newSegment.points));
            }
        }

        mNotificationHelper.updateNotification(mTrackHelper);
        EventBus.getDefault().post(new TimerUpdateEvent(mTrackHelper));

        if (mShutdownInterval != -1L && mTrackHelper.getTotalSecond() >= mShutdownInterval) {
            EventBus.getDefault().postSticky(new ShutdownEvent());
        }
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void postRouterUpdates(RequestRouteUpdateEvent request) {
        EventBus.getDefault().post(new RouteUpdateEvent(mTrackHelper.getRoute()));
    }

    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
        StringBuilder locationDataBuilder = new StringBuilder();

        locationDataBuilder.append("Сырые данные:\n");
        // locationDataBuilder.append(StringUtils.getLocationDataText(mRawLocationData));
        locationDataBuilder.append("Отфильтрованные данные:\n");
        // locationDataBuilder.append(mTrackHelper.toString());

        EventBus.getDefault().post(new StopRouteEvent(mTrackHelper));


        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        timerDisposable.dispose();
        timerDisposable = null;
        mTrackHelper = null;
        stopForeground(true);
    }
}
