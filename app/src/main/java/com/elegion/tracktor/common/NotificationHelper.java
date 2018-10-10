package com.elegion.tracktor.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.elegion.tracktor.R;
import com.elegion.tracktor.service.INotificationHelper;
import com.elegion.tracktor.service.ITrackHelper;
import com.elegion.tracktor.ui.map.MainActivity;
import com.elegion.tracktor.utils.IDistanceConverter;
import com.elegion.tracktor.utils.StringUtils;

import static android.content.Context.NOTIFICATION_SERVICE;


public class NotificationHelper implements INotificationHelper {
    public static final String NOTIFICATION_CHANNEL_ID = "TRACKTOR_CHANNEL_ID";

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private String mTimeLabel;
    private String mDistanceLabel;
    private String mSpeedLabel;
    private String mNotificationTitle;

    protected IDistanceConverter mDistanceConverter;

    public NotificationHelper(Context context, IDistanceConverter distanceConverter) {
        mDistanceConverter = distanceConverter;
        mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotificationBuilder = getNotificationBuilder(context);

        mTimeLabel = context.getString(R.string.timeLabel);
        mDistanceLabel = context.getString(R.string.distanceLabel);
        mSpeedLabel = context.getString(R.string.speedLabel);
        mNotificationTitle = context.getString(R.string.notificationTitle);

    }

    private NotificationCompat.Builder getNotificationBuilder(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return new NotificationCompat.Builder(context);
        } else {
            if (mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        context.getString(R.string.notifChannelLabel),
                        NotificationManager.IMPORTANCE_LOW);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        }
    }

    public Notification getNotification(Context context){

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.putExtra("STOP_TRACK", false);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(context, MainActivity.class);
        stopIntent.setAction(Intent.ACTION_MAIN);
        stopIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        stopIntent.putExtra("STOP_TRACK", true);

        PendingIntent stopPendingIntent = PendingIntent.getActivity(context, 1, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        return mNotificationBuilder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_tracktor)
                .setContentTitle(mNotificationTitle)
                .setWhen(System.currentTimeMillis())
                .setColor(context.getResources().getColor(R.color.colorRouteLine))
                .addAction(R.drawable.ic_stop_white_24dp, "Остановить", stopPendingIntent)
                .build();
    }

    public void updateNotification(ITrackHelper trackHelper) {
        StringBuilder contentText = new StringBuilder();
        contentText//.append(getString(R.string.notificationText)).append('\n')
                .append(mTimeLabel)
                .append(StringUtils.getDurationText(trackHelper.getTotalSecond()))
                .append(" ")
                .append(mDistanceLabel)
                .append(mDistanceConverter.convertDistance(trackHelper.getDistance()))
                .append(" ")
                .append(mSpeedLabel)
                .append(mDistanceConverter.convertSpeed(trackHelper.getAverageSpeed()));

        mNotificationBuilder.setContentText(contentText.toString())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText.toString()))
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(DEFAULT_NOTIFICATION_ID, mNotificationBuilder.build());
    }
}
