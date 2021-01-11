package com.example.unknownuser.scrapstable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

/**
 * Created by UnknownUser on 2017-10-26.
 */

public class ImageSelectActivity extends AppCompatActivity
{
    private GridView grid;
    public static CustomGridAdapter adapter;

    public static Integer[] imgid={
            R.drawable.meal,
            R.drawable.breakfast,
            R.drawable.lunch,
            R.drawable.dinner,
            R.drawable.dessert,
            R.drawable.burger,
            R.drawable.chicken,
            R.drawable.dairy,
            R.drawable.fish,
            R.drawable.fruit,
            R.drawable.pasta,
            R.drawable.pastry,
            R.drawable.pizza,
            R.drawable.sandwich,
            R.drawable.vegetable};

    public String[] categories;
    public static final String IMAGEID = "ImageID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageselect);

        categories = getResources().getStringArray(R.array.category);
        adapter = new CustomGridAdapter(this, imgid, categories);

        grid=(GridView)findViewById(R.id.gridview);
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent();
                //startActivity(intent);
                intent.putExtra(IMAGEID, position);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
