package com.elegion.tracktor.job;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.elegion.tracktor.common.CurrentPreferences;
import com.elegion.tracktor.common.KalmanRoute;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.NotificationHelper;
import com.elegion.tracktor.common.event.PreferencesChangeEvent;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.common.event.StartRouteEvent;
import com.elegion.tracktor.common.event.TimerUpdateEvent;
import com.elegion.tracktor.data.IRepository;
import com.elegion.tracktor.service.ITrackHelper;
import com.elegion.tracktor.service.ITrackHelperCallBack;
import com.elegion.tracktor.ui.common.WeatherUpdater;
import com.elegion.tracktor.ui.map.MainActivity;
import com.evernote.android.job.Job;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

public class LocationJob extends Job implements ITrackHelperCallBack {

    @Inject
    protected WeatherUpdater mWeatherUpdater;
    @Inject
    protected CurrentPreferences mCurrentPreferences;
    @Inject
    protected IRepository mRepository;

    private ITrackHelper mTrackHelper;
    private Long mShutdownInterval = -1L;
    public static final String TAG = "LocationJobTag";
    public static final String RESCHEDULE_KEY = "LocationJobRescheduleKey";
    private final CountDownLatch doneSignal = new CountDownLatch(1);
    private static final int DONE_SIGNAL_WAIT_INTERVAL = 30;
    private final JobLocationCallback mCallback = locationResult -> {
        if (locationResult != null) {
            Location location = locationResult.getLastLocation();
            LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());
            mTrackHelper.onRouteUpdate(newPosition);
            Log.d(TAG, "onLocationResult: " + locationResult.toString());
        }
    };

    public LocationJob() {
        Scope scope = Toothpick.openScope("Application");
        Toothpick.inject(this, scope);
        mTrackHelper = new KalmanRoute(mWeatherUpdater);
        mTrackHelper.setCallBack(this);
        mShutdownInterval = mCurrentPreferences.getShutdownInterval();
    }

    @Override
    @NonNull
    protected Result onRunJob(Params params) {
        mTrackHelper.loadFromRealm(mRepository);
        LocationThread thread = new LocationThread("locationThread", getContext(), mCallback);
        thread.start();
        try {
            doneSignal.await(DONE_SIGNAL_WAIT_INTERVAL, TimeUnit.SECONDS);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (params.getExtras().getBoolean(RESCHEDULE_KEY, false)) {
            return Result.RESCHEDULE;
        } else {
            return Result.SUCCESS;
        }

    }

    private class LocationThread extends HandlerThread {

        private static final int UPDATE_INTERVAL = 5000;
        private static final int UPDATE_FASTEST_INTERVAL = 2000;
        private static final int UPDATE_MIN_DISTANCE = 10;

        private FusedLocationProviderClient mFusedLocationProviderClient;
        private LocationRequest mLocationRequest;
        private JobLocationCallback mCallback;


        public LocationThread(String name, Context context, JobLocationCallback callback) {
            super(name);
            mCallback = callback;
            init(context);
        }

        private void init(Context context) {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(UPDATE_FASTEST_INTERVAL);
            mLocationRequest.setSmallestDisplacement(UPDATE_MIN_DISTANCE);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            Looper.prepare();
            try {
                mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (mCallback != null) {
                            mCallback.onLocationResult(locationResult);
                        }
                        quit();
                    }
                }, Looper.myLooper());
            } catch (Throwable e) {
                e.printStackTrace();
            }
            Looper.loop();
        }
    }

    interface JobLocationCallback {
        void onLocationResult(LocationResult locationResult);
    }

    @Override
    public void onMetricsUpdate() {
        // mNotificationHelper.updateNotification(mTrackHelper);
        mTrackHelper.saveToRealm(mRepository);
        EventBus.getDefault().post(new TimerUpdateEvent(mTrackHelper));

        if (mShutdownInterval != -1L && mTrackHelper.getTotalSecond() >= mShutdownInterval) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.putExtra("STOP_TRACK", true);
            getContext().startActivity(intent);
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

//    @Subscribe(threadMode = ThreadMode.ASYNC)
//    public void onUpdateShutdownInterval(PreferencesChangeEvent event) {
//        long shutdownInterval = mCurrentPreferences.getShutdownInterval();
//        if (mShutdownInterval != shutdownInterval) {
//            mShutdownInterval = shutdownInterval;
//        }
//    }
}
