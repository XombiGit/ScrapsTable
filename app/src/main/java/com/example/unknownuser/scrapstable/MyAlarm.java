package com.example.unknownuser.scrapstable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyAlarm extends BroadcastReceiver
{
    String label;
    int code;
    //Broadcast is set to listen to the system
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        label = intent.getStringExtra(EdibleService.SCRAPS);
        code = intent.getIntExtra(EdibleService.CODE, 0);

        Intent i = new Intent(context, EdibleService.class);

        i.putExtra(EdibleService.SCRAPS, label);
        i.putExtra(EdibleService.CODE, code);

        context.startService(i);
    }
}