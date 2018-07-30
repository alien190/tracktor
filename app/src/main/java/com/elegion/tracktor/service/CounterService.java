package com.elegion.tracktor.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.elegion.tracktor.R;
import com.elegion.tracktor.common.KalmanRoute;
import com.elegion.tracktor.common.LocationData;
import com.elegion.tracktor.common.event.SegmentForRouteEvent;
import com.elegion.tracktor.ui.map.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CounterService extends Service {


    public static final int UPDATE_INTERVAL = 5000;
    public static final int UPDATE_FASTEST_INTERVAL = 2000;
    public static final int UPDATE_MIN_DISTANCE = 10;
    private List<LocationData> mRawLocationData = new ArrayList<>();

    private int mTotalSecond;

    private KalmanRoute mKalmanRoute;
    private Disposable timerDisposable;
    private double mDistance;

    private NotificationManagerCompat mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;

    public static final int DEFAULT_NOTIFICATION_ID = 101;

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
        mNotificationBuilder = new NotificationCompat.Builder(this);
        mNotificationManager = NotificationManagerCompat.from(this);

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

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Send Foreground Notification
        sendNotification("Ticker", "Title", "Text");

        //return Service.START_STICKY;
        return START_REDELIVER_INTENT;
    }

    public void sendNotification(String Ticker, String Title, String Text) {

        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        mNotificationBuilder.setContentIntent(contentIntent)
                .setOngoing(true)   //Can't be swiped out
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.large))   // большая картинка
                .setTicker(Ticker)
                .setContentTitle(Title) //Заголовок
                .setContentText(Text) // Текст уведомления
                .setWhen(System.currentTimeMillis());

        Notification notification;
        if (android.os.Build.VERSION.SDK_INT <= 15) {
            notification = mNotificationBuilder.getNotification(); // API-15 and lower
        } else {
            notification = mNotificationBuilder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_ID, notification);

    }

    private void onTimerUpdate(int totalSeconds) {
        mTotalSecond = totalSeconds;
        mNotificationBuilder.setContentText(String.valueOf(mTotalSecond));
        mNotificationManager.notify(DEFAULT_NOTIFICATION_ID, mNotificationBuilder.build());

        if (mRawLocationData.size() != 0) {
            mKalmanRoute.onRouteUpdate(new LocationData(mRawLocationData.get(mRawLocationData.size() - 1),
                    totalSeconds));
            SegmentForRouteEvent newSegment = mKalmanRoute.getLastSegment();
            if (newSegment != null) {
                mDistance += newSegment.getSegmentDistance();
            }
        }
    }

    @Override
    public void onDestroy() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        timerDisposable.dispose();
        timerDisposable = null;
        mKalmanRoute = null;
    }
}
