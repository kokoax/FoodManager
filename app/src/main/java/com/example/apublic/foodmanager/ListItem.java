package com.example.apublic.foodmanager;

/**
 * Created by public on 2016/12/20.
 */
import android.graphics.Bitmap;

import java.util.ArrayList;
public class ListItem {

    private int id;
    private Bitmap imageBit;
    private String title;
    private String exp;

    public ListItem(int id, String title, String exp, Bitmap imageBit ){
        this.id = id;
        this.title = title;
        this.exp = exp;
        this.imageBit = imageBit;
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Bitmap getImageBit() {
        return this.imageBit;
    }
    public void setImageBit(Bitmap imageBit) {
        this.imageBit = imageBit;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getExp() {
        return this.exp;
    }
    public void setExp(String exp) {
        this.exp = exp;
    }
}