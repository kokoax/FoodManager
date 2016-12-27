package com.example.apublic.foodmanager;

import com.example.apublic.foodmanager.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by public on 2016/12/20.
 */


public class ImageArrayAdapter extends ArrayAdapter<ListItem> {

    private int resourceId;
    private List<ListItem> items;
    private LayoutInflater inflater;

    public ImageArrayAdapter(Context context, int resourceId, List<ListItem> items) {
        super(context, resourceId, items);

        this.resourceId = resourceId;
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ListItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = this.inflater.inflate(this.resourceId, null);
        }

        ListItem item = this.items.get(position);

        // アイコンをセット
        ImageView appInfoImage = (ImageView) view.findViewById(R.id.item_image);
        appInfoImage.setImageBitmap(item.getImageBit());

        // テキストをセット
        TextView appInfoTitle = (TextView) view.findViewById(R.id.item_title);
        appInfoTitle.setText(item.getTitle());

        // テキストをセット
        TextView appInfoExp = (TextView) view.findViewById(R.id.item_exp);
        appInfoExp.setText(item.getExp());

        // 消費期限切れのアイテムの背景色を変える
        int backgroundColor;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar exp_cal = Calendar.getInstance();
        try {
            exp_cal.setTime(formatter.parse(item.getExp()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (getDiffDays(exp_cal, Calendar.getInstance()) < 0) {
            view.setBackgroundColor(Color.rgb(255,182,193));
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        return view;
    }

    private int getDiffDays(Calendar calendar1, Calendar calendar2) {
        //==== ミリ秒単位での差分算出 ====//
        long diffTime = calendar1.getTimeInMillis() - calendar2.getTimeInMillis();

        //==== 日単位に変換 ====//
        int MILLIS_OF_DAY = 1000 * 60 * 60 * 24;
        int diffDays = (int) (diffTime / MILLIS_OF_DAY);

        return diffDays;
    }
}
