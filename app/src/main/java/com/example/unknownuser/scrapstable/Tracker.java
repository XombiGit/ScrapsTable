package com.example.unknownuser.scrapstable;

import android.graphics.Bitmap;

/**
 * Created by UnknownUser on 2017-08-23.
 */

public class Tracker
{
    private String label;
    private long counter;
    private int picID;
    private int code;
    private long halfway;
    private long fifteen;

    public Tracker(String label, long counter, int picID, int code, long halfway, long fifteen)
    {
        this.label = label;
        this.counter = counter;
        this.picID = picID;
        this.code = code;
        this.halfway = halfway;
        this.fifteen = fifteen;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public long getCounter()
    {
        return counter;
    }

    public void setCounter(long counter)
    {
        this.counter = counter;
    }

    public int getID()
    {
        return picID;
    }

    public void setID(int picID)
    {
        this.picID = picID;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getHalfway() {
        return halfway;
    }

    public void setHalfway(long halfway) {
        this.halfway = halfway;
    }

    public long getFifteen() {
        return fifteen;
    }

    public void setFifteen(long fifteen) {
        this.fifteen = fifteen;
    }
}
