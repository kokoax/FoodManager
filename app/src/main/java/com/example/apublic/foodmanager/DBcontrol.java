package com.example.apublic.foodmanager;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.io.ByteArrayOutputStream;

import java.util.Date;
import java.text.DateFormat;

/**
 * Created by public on 2016/10/15.
 */

public class DBcontrol {

    static final String DATABASE_NAME = "foodManage.db";
    static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "foodData";
    public static final String COL_ID = "_id";
    public static final String COL_FOOD_NAME = "foodName";
    public static final String COL_FOOD_EX = "foodEx";
    public static final String COL_FOOD_IMG = "foodImg";
    public static final String COL_LASTUPDATE = "lastupdate";

    protected final Context context;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    public DBcontrol(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    //
    // SQLiteOpenHelper
    //

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE_NAME + " ("
                            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COL_FOOD_NAME + " TEXT NOT NULL,"
                            + COL_FOOD_EX   + " TEXT NOT NULL,"
                            + COL_FOOD_IMG    + " BLOB NOT NULL,"
                            + COL_LASTUPDATE + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(
                SQLiteDatabase db,
                int oldVersion,
                int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

    }

    //
    // Adapter Methods
    //

    public DBcontrol open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }


    public void close(){
        dbHelper.close();
    }


    //
    // App Methods
    //


    public boolean deleteAllFoodDatas(){
        return db.delete(TABLE_NAME, null, null) > 0;
    }

    public boolean deleteFoodData(int id){
        return db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
    }

    public Cursor getAllFoodData(){
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }


    public void saveFoodData(String name, String ex, Bitmap image){
        Date dateNow = new Date ();
        ContentValues values = new ContentValues();

        values.put(COL_FOOD_NAME, name);
        values.put(COL_FOOD_EX, ex);
        values.put(COL_FOOD_IMG, bitmap2byteArray(image));
        // System.out.println(DateFormat.getDateInstance().format(dateNow));
        values.put(COL_LASTUPDATE, DateFormat.getDateInstance().format(dateNow));

        db.insertOrThrow(TABLE_NAME, null, values);
    }

    public Bitmap getBitmapFromByteArray(byte[] bytes, Resources resource) {
        return ((BitmapDrawable) new BitmapDrawable(resource, BitmapFactory.decodeByteArray(bytes, 0, bytes.length))).getBitmap();
    }

    private byte[] bitmap2byteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}