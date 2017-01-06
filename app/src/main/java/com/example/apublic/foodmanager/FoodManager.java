package com.example.apublic.foodmanager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.R.attr.bitmap;

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
                startActivity(intent);
            }
        });

        viewFoods();
        setLocalNotification(this, "TEST", 30);
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
                foodList.add(new ListItem(
                        cursor.getInt(cursor.getColumnIndex("_id")), cursor.getString(cursor.getColumnIndex("foodName")),
                        cursor.getString(cursor.getColumnIndex("foodEx")), foodDB.getBitmapFromByteArray(cursor.getBlob(cursor.getColumnIndex("foodImg")), getResources())));
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
        switch (item.getItemId()) {
            case CONTEXT_MENU_EDIT:
                // 食品を追加するボタンを押すと,食品追加画面へ移動する.
                Intent intent = new Intent(FoodManager.this, AddFood.class);

                intent.putExtra("ID", (int) ((ListItem) adapter.getItem(info.position)).getId());

                intent.putExtra("TITLE", (String) ((ListItem) adapter.getItem(info.position)).getTitle());

                intent.putExtra("EXP", (String) ((ListItem) adapter.getItem(info.position)).getExp());

                // Bitmapを直にputExtraするとメモリリーク的な事が起こり落ちるため,画像を一時保存してそのパスを渡している.
                String timeStamp = (String) (new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(Calendar.getInstance().getTime()));
                String imageName = timeStamp + ".png";
                File imageFile = new File(this.getFilesDir(), imageName);
                FileOutputStream out;
                try {
                    out = new FileOutputStream(imageFile);
                    ((ListItem) adapter.getItem(info.position)).getImageBit().compress(Bitmap.CompressFormat.PNG, 0, out);
                    ///画像をアプリの内部領域に保存
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                intent.putExtra("IMAGE",
                        imageFile.getAbsolutePath());

                startActivity(intent);

                return true;
            case CONTEXT_MENU_DELETE:
                foodDB.deleteFoodDataFromId((int)((ListItem) adapter.getItem(info.position)).getId());
                adapter.remove(adapter.getItem(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void setLocalNotification(Context context, String message, int requestCode) {
        Intent intent = new Intent(context, Notifier.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 30, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());

        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        long target_ms = calendar.getTimeInMillis();
        long now_ms = Calendar.getInstance().getTimeInMillis();

        //今日ならそのまま指定
        if (target_ms >= now_ms) {
            // AlarmManager.RTC_WAKEUPで端末スリープ時に起動させるようにする
            alarmManager.set(AlarmManager.RTC_WAKEUP, target_ms, sender);
            //過ぎていたら明日の同時刻を指定
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            target_ms = calendar.getTimeInMillis();
            alarmManager.set(AlarmManager.RTC_WAKEUP, target_ms, sender);
        }
    }

    private LinearLayout.LayoutParams createParam(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }
}
