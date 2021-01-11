package com.example.unknownuser.scrapstable;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by UnknownUser on 2017-10-26.
 */

public class CustomGridAdapter extends ArrayAdapter
{
    private final Activity context;
    Integer[] images;
    String[] names;

    public CustomGridAdapter(@NonNull Activity context, Integer[] images, String[] names)
    {
        super(context, R.layout.gridview, images);
        this.context = context;
        this.images = images;
        this.names = names;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.gridview, null,true);

        ImageView image = (ImageView)rowView.findViewById(R.id.image);
        image.setImageResource(images[position]);

        TextView text = (TextView)rowView.findViewById(R.id.text);
        text.setText(names[position]);
        return rowView;
    }
}
