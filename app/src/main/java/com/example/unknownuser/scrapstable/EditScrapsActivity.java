package com.example.unknownuser.scrapstable;

import android.app.Activity;
import android.app.AlarmManager;
import android.support.v7.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

/**
 * Created by UnknownUser on 2017-08-30.
 */

public class EditScrapsActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE = 1000;
    public static final String INDEX = "Index";
    public static final String DESCRIPTION = "Description";
    public static final String COUNTDOWN = "Countdown";
    public static final String HALFTIME = "Halfway";
    public static final String FIFTIME = "Fifteen";
    public static final String IMAGE = "Image";
    public static final String CODE = "Code";

    private ImageView imageView;
    private Uri imageUri;
    String description;
    long timeToCount;
    int index;
    int imageID;
    int newIndex;
    long halfTime;
    long timeFifteen = 0;
    final long fifteen = 1000 * 60 * 15;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Gson gson;
    String json;
    ArrayList<Tracker> temp;
    Spinner spinner;

    Intent alarmIntent;
    AlarmManager alarm;
    PendingIntent pendingIntent;
    Intent newAlarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editscraps);

        spinner = (Spinner) findViewById(R.id.expireSpinnerEdit);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_array, R.layout.spinner);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        imageID = getIntent().getIntExtra(IMAGE, 0);
        imageView = (ImageView)findViewById(R.id.imageEdit);
        imageView.setImageResource(ImageSelectActivity.imgid[imageID]);

        index = getIntent().getIntExtra(INDEX, 0);

        View button = findViewById(R.id.addButtonEdit);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                EditText label = (EditText) findViewById(R.id.titleInputEdit);
                ImageView pic = (ImageView) findViewById(R.id.imageEdit);
                EditText expire = (EditText) findViewById(R.id.expireInputEdit);

                sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE);
                gson = new Gson();
                json = sharedPref.getString("LeftoverList", " ");

                TypeToken<ArrayList<Tracker>> token = new TypeToken<ArrayList<Tracker>>() {};
                temp = gson.fromJson(json, token.getType());

                description = label.getText().toString();
                String timeInput = expire.getText().toString();

                if((description.trim().length() == 0 && timeInput.trim().length() == 0 && imageID == temp.get(index).getID()) ||
                        (description.equals(temp.get(index).getLabel()) && timeInput.trim().length() == 0 && imageID == temp.get(index).getID()) ||
                        timeInput.equals("0") || timeInput.equals("00"))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(EditScrapsActivity.this, R.style.ScrapAlert));
                    builder.setTitle("Warning !");
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setMessage("No changes were made.  Remember 0 is not a valid value.  If you would like to reset the timer, just enter the same value again.");
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
                    editor = sharedPref.edit();
                    Intent data = new Intent();

                    if(!description.equals(temp.get(index).getLabel()) && description.trim().length() != 0)
                    {
                        Log.i("DESCRIPTION", "WORKS !!!");
                        description = description.substring(0,1).toUpperCase() + description.substring(1).toLowerCase();

                        temp.get(index).setLabel(description);
                        data.putExtra(DESCRIPTION, description);

                        if(timeInput.trim().length() == 0)
                        {
                            Log.i("HALFTIME", "WORKS !!!");
                            alarmIntent = new Intent(getApplicationContext(), MyAlarm.class);
                            alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                            if (temp.get(index).getCode() >= 0) {
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.valueOf(String.valueOf(1) + String.valueOf(temp.get(index).getCode())), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarm.cancel(pendingIntent);
                                pendingIntent.cancel();

                                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), Integer.valueOf(String.valueOf(2) + String.valueOf(temp.get(index).getCode())), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarm.cancel(pendingIntent1);
                                pendingIntent1.cancel();

                                PendingIntent pend = PendingIntent.getBroadcast(getApplicationContext(), Integer.valueOf(String.valueOf(3) + String.valueOf(temp.get(index).getCode())), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarm.cancel(pend);
                                pend.cancel();
                            }

                            temp.get(index).setCode(AddScrapsActivity.index);
                            data.putExtra(CODE, AddScrapsActivity.index);

                            createNewAlarm(description, temp.get(index).getHalfway(), 1, AddScrapsActivity.index);
                            createNewAlarm(description, temp.get(index).getFifteen(), 2, AddScrapsActivity.index);
                            createNewAlarm(description, temp.get(index).getCounter(), 3, AddScrapsActivity.index);

                            AddScrapsActivity.index++;
                        }
                    }

                    if(imageID != temp.get(index).getID())
                    {
                        Log.i("IMAGE", "WORKS !!!");
                        temp.get(index).setID(imageID);
                        data.putExtra(IMAGE, imageID);
                    }

                    if(timeInput.trim().length() != 0)
                    {
                        Log.i("COUNTER", "WORKS !!!");
                        alarmIntent = new Intent(getApplicationContext(), MyAlarm.class);
                        alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

                        if (temp.get(index).getCode() >= 0) {
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.valueOf(String.valueOf(1) + String.valueOf(temp.get(index).getCode())), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarm.cancel(pendingIntent);
                            pendingIntent.cancel();

                            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(), Integer.valueOf(String.valueOf(2) + String.valueOf(temp.get(index).getCode())), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarm.cancel(pendingIntent1);
                            pendingIntent1.cancel();

                            PendingIntent pend = PendingIntent.getBroadcast(getApplicationContext(), Integer.valueOf(String.valueOf(3) + String.valueOf(temp.get(index).getCode())), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarm.cancel(pend);
                            pend.cancel();
                        }

                        int userTime = Integer.parseInt(timeInput);
                        long dayTime = 0;

                        if (spinner.getSelectedItemPosition() == 0) {
                            dayTime = 1000 * 60 * 60;
                        } else if (spinner.getSelectedItemPosition() == 1) {
                            dayTime = 1000 * 60 * 60 * 24;
                            //dayTime = 1000 * 60;
                        }

                        long totalMilli = userTime * dayTime;
                        long fifteenMark = totalMilli - fifteen;

                        timeToCount = System.currentTimeMillis() + totalMilli;
                        halfTime = System.currentTimeMillis() + (totalMilli / 2);
                        timeFifteen = System.currentTimeMillis() + fifteenMark;

                        if(description.equals(temp.get(index).getLabel()) || description.trim().length() == 0)
                        {
                            description = temp.get(index).getLabel();
                        }

                        temp.get(index).setCounter(timeToCount);
                        temp.get(index).setHalfway(halfTime);
                        temp.get(index).setFifteen(timeFifteen);
                        temp.get(index).setCode(AddScrapsActivity.index);

                        data.putExtra(COUNTDOWN, timeToCount);
                        data.putExtra(HALFTIME, halfTime);
                        data.putExtra(FIFTIME, timeFifteen);
                        data.putExtra(CODE, AddScrapsActivity.index);

                        createNewAlarm(description, halfTime, 1, AddScrapsActivity.index);
                        createNewAlarm(description, timeFifteen, 2, AddScrapsActivity.index);
                        createNewAlarm(description, timeToCount, 3, AddScrapsActivity.index);

                        AddScrapsActivity.index++;
                    }

                    String json = gson.toJson(temp);
                    editor.putString("LeftoverList", json);
                    editor.commit();

                    data.putExtra(INDEX, index);
                    setResult(Activity.RESULT_OK, data);
                    finish();// close activity
                }
            }
        });
    }

    public void imageSelect(View view)
    {
        Intent intent = new Intent(this, ImageSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void returnBack(View view)
    {
        super.onBackPressed();
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

    public void createNewAlarm(String title, long countdown, int code, int row)
    {
        newAlarmIntent = new Intent(this, MyAlarm.class);
        //alarmIntent.putExtra("Food", "Food1");
        newAlarmIntent.putExtra(EdibleService.SCRAPS, title);
        newAlarmIntent.putExtra(EdibleService.CODE, code);
        newAlarmIntent.putExtra(EdibleService.INDEX, row);

        pendingIntent = PendingIntent.getBroadcast(this, Integer.valueOf(String.valueOf(code) + String.valueOf(row)), newAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarm.set(AlarmManager.RTC_WAKEUP, countdown, pendingIntent);
    }
}
