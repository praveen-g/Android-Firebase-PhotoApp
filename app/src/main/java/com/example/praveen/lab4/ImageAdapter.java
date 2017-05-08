package com.example.praveen.lab4;

import android.view.View;
import android.net.Uri;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.content.Context;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;


/**
 * Created by praveen on 5/5/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mThumbIds;

    public ImageAdapter(Context c,String[] photoUrls) {

        mContext = c;
        mThumbIds = photoUrls;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(155, 155));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
        } else {
            imageView = (ImageView) convertView;
        }

        Glide
                .with(mContext)
                .load(mThumbIds[position])
                .into(imageView);

        return imageView;
    }
}
