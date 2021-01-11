package com.example.unknownuser.scrapstable;

/**
 * Created by UnknownUser on 2017-08-24.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.support.v7.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.runner.BaseTestRunner;

import java.util.ArrayList;

import static android.content.Context.ALARM_SERVICE;

public class CustomListAdapter extends ArrayAdapter<Tracker>{

    private final Activity context;
    public static ArrayList<Tracker> labels = new ArrayList<Tracker>();
    int pos;
    public static ArrayList<Tracker> temp = new ArrayList<Tracker>();
    long timer;

    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    Gson gson;
    String json;

    Intent alarmIntent;
    AlarmManager alarm;
    PendingIntent pendingIntent;

    public static boolean deleted = false;

    public CustomListAdapter(Activity context, ArrayList<Tracker> label)
    {
        super(context, R.layout.listview, label);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.labels =label;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        pos = position;
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.listview, null,true);

        if(view != null && view.getId() == R.id.textView1)
        {
            Log.i("aaa", "I was Called");
        }

        timer = labels.get(position).getCounter();

        Button delete = (Button)rowView.findViewById(R.id.deleteButton);
        delete.setTag(position);
        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v) {

                final Integer index = (Integer) v.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.ScrapDialog));
                builder.setCancelable(true);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("Scraps Table");
                builder.setMessage("Are you sure you want to delete this tracker ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                        alarmIntent = new Intent(getContext(), MyAlarm.class);
                        alarm = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);

                        sharedPref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
                        gson = new Gson();
                        json = sharedPref.getString("LeftoverList", " ");

                        TypeToken<ArrayList<Tracker>> token = new TypeToken<ArrayList<Tracker>>() {};
                        temp = gson.fromJson(json, token.getType());

                        if(temp.get(index).getCode() >= 0) {
                            pendingIntent = PendingIntent.getBroadcast(getContext(), Integer.valueOf(String.valueOf(1) + String.valueOf(temp.get(index).getCode())), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarm.cancel(pendingIntent);
                            pendingIntent.cancel();

                            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getContext(), Integer.valueOf(String.valueOf(2) + String.valueOf(temp.get(index).getCode())), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarm.cancel(pendingIntent1);
                            pendingIntent1.cancel();

                            PendingIntent pend = PendingIntent.getBroadcast(getContext(), Integer.valueOf(String.valueOf(3) + String.valueOf(temp.get(index).getCode())), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarm.cancel(pend);
                            pend.cancel();
                        }

                        editor = sharedPref.edit();
                        temp.remove(index.intValue());

                        String json = gson.toJson(temp);
                        editor.putString("LeftoverList", json);
                        editor.commit();

                        labels.remove(index.intValue());
                        deleted = true;
                        notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                // Create the AlertDialog object and return it
                builder.create().show();
            }
        });

        return rowView;

    }
}