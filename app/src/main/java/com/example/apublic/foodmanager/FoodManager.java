package com.example.apublic.foodmanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.example.apublic.foodmanager.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class FoodManager extends AppCompatActivity {
    private final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    static final int CONTEXT_MENU_EDIT = 0;
    static final int CONTEXT_MENU_DELETE = 1;

    private ListView foodView;

    private DBcontrol foodDB;

    FloatingActionButton addButton;

    private List<ListItem> foodList = new ArrayList<ListItem>();
    ImageArrayAdapter adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_manager);

        foodDB = new DBcontrol(this);
        addButton = (FloatingActionButton) findViewById(R.id.addButton);

        foodDB.open();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 食品を追加するボタンを押すと,食品追加画面へ移動する.
                Intent intent = new Intent(FoodManager.this, AddFood.class);
                startActivityForResult(intent, 0);
            }
        });

        viewFoods();
        setLocalNotification(this, "TEST", 0, 10);
        // sendNotification();
    }

    protected void onRestart() {
        viewFoods();

        super.onRestart();
    }

    // トップ画面を開く際にDBからすべての食材のデータを表示する.
    private void viewFoods() {
        if (adapter != null) {
            this.adapter.clear();
        }

        adapter = new ImageArrayAdapter(this, R.layout.food_list, foodList);
        Cursor cursor = foodDB.getAllFoodData();
        try {
            while (cursor.moveToNext()) {
                foodList.add(new ListItem(cursor.getInt(0), cursor.getString(1), cursor.getString(2), foodDB.getBitmapFromByteArray(cursor.getBlob(3), getResources())));
            }
        } finally {
            foodView = (ListView) findViewById(R.id.foodListL);
            foodView.setAdapter(adapter);
            registerForContextMenu(foodView);

            cursor.close();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        //コンテキストメニューの設定
        menu.setHeaderTitle("メニュータイトル");
        //Menu.add(int groupId, int itemId, int order, CharSequence title)
        menu.add(0, CONTEXT_MENU_EDIT, 0, "Edit");
        menu.add(0, CONTEXT_MENU_DELETE, 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        System.out.println(getBaseContext());
        switch (item.getItemId()) {
            case CONTEXT_MENU_EDIT:
                return true;
            case CONTEXT_MENU_DELETE:
                adapter.remove(adapter.getItem(info.position));
                // System.out.println(adapter.get(info.position));
                // System.out.println(foodList.get(info.position).getId());
                // deleteList(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void sendNotification() {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(getApplicationContext().getPackageName(), FoodManager.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle("Title!")
                .setContentText("Content Text")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.meet)
                .setAutoCancel(true)
                .build();

        NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1000, notification);
    }

    public static void setLocalNotification(Context context, String message, int requestCode, int interval) {
        Intent intent = new Intent(context, Notifier.class);
        intent.putExtra("MESSAGE", message);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getDefault());

        /*
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 42);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // AlarmManager.RTC_WAKEUPで端末スリープ時に起動させるようにする
        // 1回だけ通知の場合はalarmManager.set()を使う
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
            // 一日毎にアラームを呼び出す
                AlarmManager.INTERVAL_DAY, sender);
                */
        calendar.setTimeInMillis(System.currentTimeMillis()); // 現在時刻を取得
        calendar.add(Calendar.SECOND, 10);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        Toast.makeText(context.getApplicationContext(), "Alarm Setting", Toast.LENGTH_SHORT).show();
    }

    private LinearLayout.LayoutParams createParam(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }
}
