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

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return mNotificationBuilder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_tracktor)
                .setContentTitle(mNotificationTitle)
                .setWhen(System.currentTimeMillis())
                .setColor(context.getResources().getColor(R.color.colorRouteLine))
                .build();
    }

    public void updateNotification(int totalSecond, double distance, double averageSpeed) {
        StringBuilder contentText = new StringBuilder();
        contentText//.append(getString(R.string.notificationText)).append('\n')
                .append(mTimeLabel)
                .append(StringUtils.getDurationText(totalSecond))
                .append(" ")
                .append(mDistanceLabel)
                .append(mDistanceConverter.convertDistance(distance))
                .append(" ")
                .append(mSpeedLabel)
                .append(mDistanceConverter.convertSpeed(averageSpeed));

        mNotificationBuilder.setContentText(contentText.toString())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText.toString()))
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(DEFAULT_NOTIFICATION_ID, mNotificationBuilder.build());
    }
}
