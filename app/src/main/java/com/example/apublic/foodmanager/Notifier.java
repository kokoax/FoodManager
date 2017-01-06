package com.example.apublic.foodmanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

import com.example.apublic.foodmanager.R;

/**
 * Created by public on 2016/12/22.
 */
public class Notifier extends BroadcastReceiver {
    private String message;
    private DBcontrol foodDB;

    @Override
    public void onReceive(Context context, Intent intent_tmp) {
        // message = intent_tmp.getStringExtra("MESSAGE");
        foodDB = new DBcontrol(context);
        foodDB.open();

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(context.getApplicationContext().getPackageName(), FoodManager.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 20, intent, 0);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(String.valueOf(foodDB.getImminetFoods().size()) + "個の食材の賞味期限が迫っています")
                .setContentText("aaa\nbbb\n")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.noimage)
                .setAutoCancel(true)
                .build();

        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1000, notification);
        long now_ms = Calendar.getInstance().getTimeInMillis();

        // 次の通知を設定
        Intent next_notice = new Intent(context, Notifier.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 30, next_notice, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());

        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // 明日の通知
        calendar.add(Calendar.DAY_OF_MONTH, 1);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }
}
