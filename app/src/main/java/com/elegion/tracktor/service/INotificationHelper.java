package com.elegion.tracktor.service;

import android.app.Notification;
import android.content.Context;

public interface INotificationHelper {
    int DEFAULT_NOTIFICATION_ID = 101;
    Notification getNotification(Context context);
    void updateNotification(ITrackHelper trackHelper);
}
