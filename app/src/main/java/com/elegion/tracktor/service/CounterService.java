package com.elegion.tracktor.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.KalmanRoute;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.NotificationHelper;
import com.elegion.tracktor.common.event.GoToBackgroundEvent;
import com.elegion.tracktor.common.event.PreferencesChangeEvent;
import com.elegion.tracktor.common.event.ReadyToBackground;
import com.elegion.tracktor.common.event.RequestRouteUpdateEvent;
import com.elegion.tracktor.common.event.RouteUpdateEvent;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.common.event.StartRouteEvent;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.common.event.TimerUpdateEvent;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.ui.common.WeatherUpdater;
import com.elegion.tracktor.ui.map.MainActivity;
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

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

public class CounterService extends Service implements ITrackHelperCallBack {

    public static final int UPDATE_INTERVAL = 5000;
    public static final int UPDATE_FASTEST_INTERVAL = 2000;
    public static final int UPDATE_MIN_DISTANCE = 10;

    private ITrackHelper mTrackHelper;

    private Long mShutdownInterval = -1L;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    private boolean mIsResume;
    private boolean mGoToBackground;

    @Inject
    protected IDistanceConverter mDistanceConverter;
    @Inject
    protected CurrentPreferences mCurrentPreferences;
    @Inject
    protected WeatherUpdater mWeatherUpdater;
    @Inject
    protected IRepository mRepository;

    private INotificationHelper mNotificationHelper;

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mTrackHelper.onRouteUpdate(newPosition);
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

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        startForeground();
        if (mIsResume) {
            mTrackHelper.resume();
        } else {
            mTrackHelper.start();
        }
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

    private void init() {
        Scope scope = Toothpick.openScope("Application");
        Toothpick.inject(this, scope);
        mShutdownInterval = mCurrentPreferences.getShutdownInterval();
        mNotificationHelper = new NotificationHelper(this, mDistanceConverter);
        EventBus.getDefault().register(this);
        mTrackHelper = new KalmanRoute(mWeatherUpdater);
        mIsResume = mTrackHelper.loadFromRealm(mRepository);
        mTrackHelper.setCallBack(this);
        mGoToBackground = false;
    }

    public void startForeground() {
        startForeground(NotificationHelper.DEFAULT_NOTIFICATION_ID,
                mNotificationHelper.getNotification(getApplicationContext()));
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void postRouterUpdates(RequestRouteUpdateEvent request) {
        EventBus.getDefault().post(new RouteUpdateEvent(mTrackHelper.getRoute()));
    }

    @Override
    public void onDestroy() {
        stop();
    }

    @Override
    public void onMetricsUpdate() {
        mNotificationHelper.updateNotification(mTrackHelper);
        EventBus.getDefault().post(new TimerUpdateEvent(mTrackHelper));

        if (mShutdownInterval != -1L && mTrackHelper.getTotalSecond() >= mShutdownInterval) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.putExtra("STOP_TRACK", true);
            startActivity(intent);

            //EventBus.getDefault().postSticky(new ShutdownEvent());
        }
    }

    @Override
    public void onFirstPoint(LocationData point) {
        EventBus.getDefault().post(new StartRouteEvent(point));
    }

    @Override
    public void onRouteUpdate(SegmentForRouteEvent segment) {
        EventBus.getDefault().post(new SegmentForRouteEvent(segment.points));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onUpdateShutdownInterval(PreferencesChangeEvent event) {
        long shutdownInterval = mCurrentPreferences.getShutdownInterval();
        if (mShutdownInterval != shutdownInterval) {
            mShutdownInterval = shutdownInterval;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void goToBackground(GoToBackgroundEvent event) {
        mGoToBackground = true;
        mTrackHelper.saveToRealm(mRepository);
        stop();
    }

    private void stop() {
        if (mGoToBackground) {
            EventBus.getDefault().post(new ReadyToBackground());
        } else {
            EventBus.getDefault().postSticky(new StopRouteEvent(mTrackHelper));
        }

        mTrackHelper.stop();
        EventBus.getDefault().unregister(this);
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        mTrackHelper = null;
        stopForeground(true);
    }
}
