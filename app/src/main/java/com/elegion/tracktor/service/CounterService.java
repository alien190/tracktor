package com.elegion.tracktor.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.KalmanRoute;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.event.RequestRouteUpdateEvent;
import com.elegion.tracktor.common.event.RouteUpdateEvent;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.common.event.ShutdownEvent;
import com.elegion.tracktor.common.event.StartRouteEvent;
import com.elegion.tracktor.common.event.StopRouteEvent;
import com.elegion.tracktor.common.event.TimerUpdateEvent;
import com.elegion.tracktor.ui.map.MainActivity;
import com.elegion.tracktor.utils.StringUtils;
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

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CounterService extends Service {

    public static final int UPDATE_INTERVAL = 5000;
    public static final int UPDATE_FASTEST_INTERVAL = 2000;
    public static final int UPDATE_MIN_DISTANCE = 10;
    public static final String NOTIFICATION_CHANNEL_ID = "TRACKTOR_CHANNEL_ID";
    private List<LocationData> mRawLocationData = new ArrayList<>();

    private int mTotalSecond;
    private Date mStartDate;

    private KalmanRoute mKalmanRoute;
    private Disposable timerDisposable;
    private double mDistance;
    private boolean isStartPointSend;
    private double mAverageSpeed;

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationChannel mNotificationChannel;

    public static final int DEFAULT_NOTIFICATION_ID = 101;

    private Long mShutdownInterval = -1L;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest = new LocationRequest();

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mRawLocationData.add(new LocationData(newPosition, mTotalSecond));
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
        mKalmanRoute = new KalmanRoute();
        mTotalSecond = 0;
        mDistance = 0;
        mAverageSpeed = 0;

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationBuilder = getNotificationBuilder();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mShutdownInterval = Long.valueOf(sharedPreferences.getString(getString(R.string.shutdown_key), "-1"));

        EventBus.getDefault().register(this);
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return new NotificationCompat.Builder(this);
        } else {
            if (mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        getString(R.string.notifChannelLabel),
                        NotificationManager.IMPORTANCE_LOW);

                //mNotificationChannel.setLightColor(Color.CYAN);
                //mNotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mStartDate = Calendar.getInstance().getTime();
        startForeground();

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

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationBuilder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_tracktor)
                //.setTicker(Ticker)
                .setContentTitle(getString(R.string.notificationTitle))
                .setWhen(System.currentTimeMillis())
                .setColor(getApplicationContext().getResources().getColor(R.color.colorRouteLine));

        startForeground(DEFAULT_NOTIFICATION_ID, mNotificationBuilder.build());
    }

    private void onTimerUpdate(int totalSeconds) {
        mTotalSecond = totalSeconds;

        if (!isStartPointSend && mRawLocationData.size() != 0) {
            EventBus.getDefault().post(new StartRouteEvent(mRawLocationData.get(0)));
            isStartPointSend = true;
        }

        if (mRawLocationData.size() != 0) {
            mKalmanRoute.onRouteUpdate(new LocationData(mRawLocationData.get(mRawLocationData.size() - 1),
                    totalSeconds));
            SegmentForRouteEvent newSegment = mKalmanRoute.getLastSegment();
            if (newSegment != null) {
                mDistance += newSegment.getSegmentDistance();
                EventBus.getDefault().post(new SegmentForRouteEvent(newSegment.points));
            }
        }

        mAverageSpeed = mDistance / (mTotalSecond == 0 ? 1 : mTotalSecond);
        updateNotification();
        EventBus.getDefault().post(new TimerUpdateEvent(mDistance, mTotalSecond, mAverageSpeed, mStartDate));

        if (mShutdownInterval != -1L && mTotalSecond >= mShutdownInterval) {
            EventBus.getDefault().postSticky(new ShutdownEvent());
        }
    }

    private void updateNotification() {
        StringBuilder contentText = new StringBuilder();
        contentText//.append(getString(R.string.notificationText)).append('\n')
                .append(getString(R.string.timeLabel))
                .append(StringUtils.getDurationText(mTotalSecond))
                .append(" ")
                .append(getString(R.string.distanceLabel))
                .append(StringUtils.getDistanceText(mDistance))
                .append(" ")
                .append(getString(R.string.speedLabel))
                .append(StringUtils.getSpeedText(mAverageSpeed));

        mNotificationBuilder.setContentText(contentText.toString())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText.toString()))
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(DEFAULT_NOTIFICATION_ID, mNotificationBuilder.build());
//todo сделать текст уведомления мультистрчным

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void postRouterUpdates(RequestRouteUpdateEvent request) {
        EventBus.getDefault().post(new RouteUpdateEvent(mKalmanRoute.getRoute()));
    }

    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
        StringBuilder locationDataBuilder = new StringBuilder();

        locationDataBuilder.append("Сырые данные:\n");
        // locationDataBuilder.append(StringUtils.getLocationDataText(mRawLocationData));
        locationDataBuilder.append("Отфильтрованные данные:\n");
        // locationDataBuilder.append(mKalmanRoute.toString());

        EventBus.getDefault().post(new StopRouteEvent(mKalmanRoute.getRoute(), mTotalSecond,
                mDistance, locationDataBuilder.toString()));


        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        timerDisposable.dispose();
        timerDisposable = null;
        mKalmanRoute = null;
        stopForeground(true);
    }
}
