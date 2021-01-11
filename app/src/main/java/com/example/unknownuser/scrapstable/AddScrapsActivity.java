package com.example.unknownuser.scrapstable;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.x;
import static android.R.id.list;

/**
 * Created by UnknownUser on 2017-08-16.
 */

public class AddScrapsActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE = 1000;
    private ImageView imageView;
    long halfTime = 0;
    long timeToCount = 0;
    long timeFifteen = 0;
    final long fifteen = 1000 * 60 * 15;

    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;

    Intent alarmIntent;
    AlarmManager alarm;
    PendingIntent pendingIntent;
    private ArrayList<Tracker> track = new ArrayList<Tracker>();

    int imageID;
    String description;
    static int index = 0;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addscraps);

        spinner = (Spinner) findViewById(R.id.expireSpinnerAdd);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.time_array, R.layout.spinner);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        imageView = (ImageView)findViewById(R.id.image);
        imageView.setImageResource(ImageSelectActivity.imgid[0]);
    }

    public void imageSelect(View view)
    {
        Intent intent = new Intent(this, ImageSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
    public void findImage(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            imageID = data.getIntExtra(ImageSelectActivity.IMAGEID, 0);
            imageView.setImageResource(ImageSelectActivity.imgid[imageID]);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void returnBack(View view)
    {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void addScraps(View view)
    {
        EditText label = (EditText)findViewById(R.id.titleEdit);
        ImageView pic = (ImageView)findViewById(R.id.image);
        EditText expire = (EditText)findViewById(R.id.expireTimeEdit);

        description = label.getText().toString();
        String timeInput = expire.getText().toString();

        if(description.trim().length() == 0 || timeInput.trim().length() == 0 || timeInput.equals("0") || timeInput.equals("00"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.ScrapAlert));
            builder.setTitle("Warning !");
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("Please enter a value for the descriptive title and a value greater than zero for time till expiration.");
            builder.setCancelable(true);
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener()
                {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                });
            builder.create().show();
        }
        else
        {
            description = description.substring(0,1).toUpperCase() + description.substring(1).toLowerCase();


            int userTime = Integer.parseInt(timeInput);
            long dayTime = 0;

            if (spinner.getSelectedItemPosition() == 0) {
                dayTime = 1000 * 60 * 60;
                //dayTime = 1000 * 60;
            } else if (spinner.getSelectedItemPosition() == 1) {
                dayTime = 1000 * 60 * 60 * 24;
            }

            long totalMilli = userTime * dayTime;
            long fifteenMin = totalMilli - fifteen;

            timeToCount = System.currentTimeMillis() + totalMilli;
            halfTime = System.currentTimeMillis() + (totalMilli / 2);
            timeFifteen = System.currentTimeMillis() + fifteenMin;

            sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE);
            String json = sharedPref.getString("LeftoverList", null);
            Gson gson = new Gson();

            if(json != null) {

                Type type = new TypeToken<ArrayList<Tracker>>() {
                }.getType();


                track = gson.fromJson(json, type);
            }

            Tracker trax = new Tracker(description, timeToCount, imageID, index, halfTime, timeFifteen);
            track.add(trax);

            json = gson.toJson(track);
            editor = sharedPref.edit();
            editor.clear();
            editor.putString("LeftoverList", json);
            editor.commit();

            alarmIntent = new Intent(this, MyAlarm.class);
            alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

            createAlarm(description, halfTime, 1, index);
            createAlarm(description, timeFifteen, 2, index);
            createAlarm(description, timeToCount, 3, index);

            index++;

            MainActivity.adapter.notifyDataSetChanged();

            super.onBackPressed();
        }
    }


    public void createAlarm(String title, long countdown, int code, int row)
    {
        alarmIntent.putExtra(EdibleService.SCRAPS, title);
        alarmIntent.putExtra(EdibleService.CODE, code);
        alarmIntent.putExtra(EdibleService.INDEX, row);

        pendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(String.valueOf(code) + String.valueOf(row)), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        alarm.set(AlarmManager.RTC_WAKEUP, countdown, pendingIntent);
    }
}
