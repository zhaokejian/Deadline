package com.example.hsq.deadline.db;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by HSQ on 15/12/10.
 */

public class DeadLine
{
    public int _id;
    public String title;
    public String category;
    public long datetime;
    public String datetimestr;
    public String settimestr;
    public long settime;
    public long alarmtime;
    public String info;
    public long isDone;
    public String alarmtimestr;

    public DeadLine()
    {
    }

    public DeadLine(String title, String category, long datetime, long settime, long alarmtime, String info, long isDone)
    {
        this.title = title;
        this.category = category;
        this.datetime = datetime;
        this.settime = settime;
        this.alarmtime = alarmtime;
        this.info = info;
        Date date = new Date(datetime);
        this.datetimestr = date.toString();
        date = new Date(settime);
        this.settimestr = date.toString();
        this.isDone = isDone;
        date = new Date(alarmtime);
        this.alarmtimestr = date.toString();
    }

}
