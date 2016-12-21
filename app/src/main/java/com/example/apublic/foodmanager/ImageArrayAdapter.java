package com.example.apublic.foodmanager;

import com.example.apublic.foodmanager.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        ImageView appInfoImage = (ImageView)view.findViewById(R.id.item_image);
        appInfoImage.setImageBitmap(item.getImageId());

        // テキストをセット
        TextView appInfoTitle = (TextView)view.findViewById(R.id.item_title);
        appInfoTitle.setText(item.getTitle());

        // テキストをセット
        TextView appInfoExp = (TextView)view.findViewById(R.id.item_exp);
        appInfoExp.setText(item.getExp());

        return view;
    }
}
