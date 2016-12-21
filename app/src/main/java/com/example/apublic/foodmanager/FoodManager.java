package com.example.apublic.foodmanager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import java.util.ArrayList;
import java.util.List;

import com.example.apublic.foodmanager.R;

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
                startActivityForResult(intent, 0);
            }
        });

        viewFoods();
    }

    protected void onRestart() {
        viewFoods();

        super.onRestart();
    }

    // トップ画面を開く際にDBからすべての食材のデータを表示する.
    private void viewFoods() {
        this.adapter.clear();
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

    private LinearLayout.LayoutParams createParam(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }
}
