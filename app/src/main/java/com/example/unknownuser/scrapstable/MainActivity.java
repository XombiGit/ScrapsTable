package com.example.unknownuser.scrapstable;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    public static CustomListAdapter adapter;

    public static ListView list ;
    public Timer timer;
    public long remaining[];

    private static final int REQUEST_CODE = 1000;

    static SharedPreferences sharedPref;

    public static ArrayList<Tracker> foodList = new ArrayList<Tracker>();

    static Type type;
    static Gson gson;
    static String json;

    String logFile = "LOGFILE.txt";


    @Override
    protected void onResume()
    {
        try
        {
            sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE);
            gson = new Gson();
            json = sharedPref.getString("LeftoverList", "");

            if(json.length() != 0) {
                type = new TypeToken<ArrayList<Tracker>>() {
                }.getType();

                foodList = gson.fromJson(json, type);
            }

            adapter = new CustomListAdapter(this, foodList);
            list=(ListView)findViewById(R.id.tracker_list);
            list.setAdapter(adapter);

        }
        catch(Exception e)
        {

        }

        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        /*writeToLog("Destroyed: " + list.getCount() + " trackers in list view\n");
        writeToLog("Destroyed: " + foodList.size() + " elements in array\n");

        String filelocation = getFilesDir() + "/" + logFile;
        //Uri path = Uri.fromFile(filelocation);
        try {
            String fileContent = getStringFromFile(filelocation);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // set the type to 'email'
            emailIntent.setType("text/plain");
            String to[] = {"harvardaidan3@hotmail.com"};
            emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
            // the attachment
            emailIntent .putExtra(Intent.EXTRA_TEXT, fileContent);
            // the mail subject
            emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject");
            startActivity(Intent.createChooser(emailIntent , "Send email..."));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        gson = new Gson();
        json = sharedPref.getString("LeftoverList", "");


        if(json.length() != 0) {
            type = new TypeToken<ArrayList<Tracker>>() {
            }.getType();

            foodList = gson.fromJson(json, type);
            }

            adapter = new CustomListAdapter(this, foodList);
            list = (ListView) findViewById(R.id.tracker_list);
            list.setAdapter(adapter);

            //writeToLog("Created: " + list.getCount() + " trackers in list view\n");
            //writeToLog("Created: " + foodList.size() + " elements in array\n");

            //deleteFile();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(MainActivity.this, EditScrapsActivity.class);
                //startActivity(intent);
                int pic = adapter.getItem(position).getID();

                intent.putExtra(EditScrapsActivity.IMAGE, pic);
                intent.putExtra(EditScrapsActivity.INDEX, position);

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        remaining = new long[]{System.currentTimeMillis() + 60000, System.currentTimeMillis() + 120000};

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {

                        if(list.getChildCount() == 0 && CustomListAdapter.labels.isEmpty() == true)
                        {
                            AddScrapsActivity.index = 0;
                        }

                        for (int i = 0; i < list.getChildCount(); i++) {

                            View v = list.getChildAt(i);
                            TextView edit = (TextView) v.findViewById(R.id.textView1);
                            TextView scribe =(TextView) v.findViewById(R.id.item);
                            ImageView pic = (ImageView) v.findViewById(R.id.icon);

                            pic.setImageResource(ImageSelectActivity.imgid[foodList.get(i).getID()]);

                            long eatTime = ((foodList.get(i).getCounter() - System.currentTimeMillis()) / 1000);

                            if (eatTime > 0) {

                                scribe.setText(foodList.get(i).getLabel());

                                int seconds = (int) (eatTime) % 60;
                                int minutes = (int) ((eatTime / 60) % 60);
                                int hours = (int) ((eatTime / (60 * 60)) % 24);
                                int days = (int) ((eatTime / (60 * 60 * 24)));
                                edit.setText(days + " D : " + hours + " H : " + minutes + " M : " + seconds + " S");

                            } else {

                                scribe.setText(foodList.get(i).getLabel());
                                edit.setText("Time to eat !!!");
                            }

                        }
                    }
                });
            }
        }, 1000, 1000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            int position = data.getIntExtra(EditScrapsActivity.INDEX, 0);

            if(data.getExtras().containsKey(EditScrapsActivity.DESCRIPTION))
            {
                String description = data.getStringExtra(EditScrapsActivity.DESCRIPTION);
                CustomListAdapter.labels.get(position).setLabel(description);
            }

            if(data.getExtras().containsKey(EditScrapsActivity.COUNTDOWN))
            {
                long countdown = data.getLongExtra(EditScrapsActivity.COUNTDOWN, 0);
                int newIndex = data.getIntExtra(EditScrapsActivity.CODE, 0);

                CustomListAdapter.labels.get(position).setCounter(countdown);
                CustomListAdapter.labels.get(position).setCode(newIndex);
            }

            if(data.getExtras().containsKey(EditScrapsActivity.IMAGE))
            {
                int imageID = data.getIntExtra(EditScrapsActivity.IMAGE, 0);
                CustomListAdapter.labels.get(position).setID(imageID);
            }

            if(data.getExtras().containsKey(EditScrapsActivity.HALFTIME))
            {
                long halfway = data.getLongExtra(EditScrapsActivity.HALFTIME, 0);
                CustomListAdapter.labels.get(position).setHalfway(halfway);
            }

            if(data.getExtras().containsKey(EditScrapsActivity.FIFTIME))
            {
                long fifteen = data.getLongExtra(EditScrapsActivity.FIFTIME, 0);
                CustomListAdapter.labels.get(position).setFifteen(fifteen);
            }

            adapter.notifyDataSetChanged();

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.add:
                if(list.getCount() >= 7)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.ScrapAlert));
                    builder.setTitle("Warning !");
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setMessage("At the moment you can have no more than 7 trackers at once.  Please be patient as I update the app.  Thank you for understanding.");
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
                    Intent intent = new Intent(MainActivity.this, AddScrapsActivity.class);
                    startActivity(intent);
                }
                break;

            /*case R.id.sortByDateCreated:
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
                break;

            case R.id.sortByDateExpiration:
                Toast.makeText(this, "Bye", Toast.LENGTH_LONG).show();
                break;*/
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void writeToLog(String log)
    {
        FileOutputStream fos;
        try
        {
            fos = openFileOutput(logFile, Context.MODE_APPEND);
            fos.write(log.getBytes());
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void deleteFile()
    {
        File dir = getFilesDir();
        File file = new File(dir, logFile);
        file.delete();
    }
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }*/
}
