package com.example.interns.hackathonservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by interns on 6/23/15.
 */
public class NotificationBuilder {
    public static final int NOTIFICATION_ID = 1;

    Context context;
    Intent intent;
    PendingIntent pendingIntent;

    public NotificationBuilder(Context context, Intent intent) {
        this.context = context;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
    }

    public void NewNotification(String alertTitle, String alertMessage) {
        Notification n  = new Notification.Builder(context)
                .setContentTitle("Hazard Nearby!")
                .setContentText(alertMessage)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, n);
    }
}
