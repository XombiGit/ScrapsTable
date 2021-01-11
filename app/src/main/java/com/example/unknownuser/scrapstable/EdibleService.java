package com.example.unknownuser.scrapstable;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class EdibleService extends IntentService
{
    public static final String SCRAPS = "Scraps";
    String meal;
    public static final String CODE = "Code";
    public static final String INDEX = "Index";
    int requestCode;
    int index;

    public EdibleService()
    {
        super("EdibleService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        meal = intent.getStringExtra(EdibleService.SCRAPS);
        requestCode = intent.getIntExtra(EdibleService.CODE, 0);
        index = intent.getIntExtra(EdibleService.INDEX, 0);

        buildNotification(meal, requestCode, index);
        stopService(intent);
    }

    public void buildNotification(String label, int request, int row)
    {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager manager = (NotificationManager)getSystemService(ns);

        int icon = R.mipmap.ic_launcher;
        CharSequence contentText = " ";
        int when = (int)System.currentTimeMillis();

        Notification.Builder builder = new Notification.Builder(this).setContentTitle(label);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            builder.setSmallIcon(R.drawable.icon_transparent);
            builder.setColor(getResources().getColor(R.color.transparentBackground));
        }
        else
        {
            builder.setSmallIcon(icon);
        }
        builder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setOnlyAlertOnce(true);

        if(request == 1)
        {
            contentText = "The timer has past the halfway mark.";
            builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));
        }
        else if (request == 2)
        {
            contentText = "The timer has past the 15 minute mark.";
            builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));
        }
        else if (request == 3)
        {
            contentText = "The timer has ended.  Click here";
            Intent notifyIntent = new Intent(this, MainActivity.class);
            PendingIntent pending = PendingIntent.getActivity(this, Integer.valueOf(String.valueOf(requestCode)+String.valueOf(index)), notifyIntent, 0);
            builder.setContentIntent(pending);
        }
        builder.setContentText(contentText);

        Notification myNotification = builder.build();

        manager.notify(when, myNotification);
    }
}
