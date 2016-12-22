package com.example.apublic.foodmanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

/**
 * Created by public on 2016/12/22.
 */
public class Notifier extends BroadcastReceiver {
    private String message;

    @Override
    public void onReceive(Context content, Intent intent_tmp) {
        message = intent_tmp.getStringExtra("MESSAGE");

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(content.getApplicationContext().getPackageName(), FoodManager.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(content, 0, intent, 0);

        Notification notification = new Notification.Builder(content)
                .setContentTitle("Title!")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.meet)
                .setAutoCancel(true)
                .build();

        NotificationManager nm = (NotificationManager)
                content.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1000, notification);
    }
}
