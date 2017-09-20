package com.example.hsq.deadline.db;

import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//参考：http://blog.csdn.net/liuhe688/article/details/6715983
public class DBManager
{
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context)
    {
        Log.d("db", "DBManager --> Constructor");
        helper = new DatabaseHelper(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add deadlines
     *
     * @param deadLines
     */
    public void addDDL(ArrayList<DeadLine> deadLines)
    {
        Log.d("db", "DBManager --> addDDL");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (DeadLine deadLine : deadLines)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME[0]
                        + " VALUES(null,?, ?, ?, ?, ?, ?,0)", new Object[] { deadLine.title,deadLine.category,
                        deadLine.datetime, deadLine.settime, deadLine.alarmtime, deadLine.info });
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }

    /**
     * add categories
     *
     * @param categories
     */
    public void addCat(ArrayList<Category> categories)
    {
        Log.d("db", "DBManager --> addCat");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try
        {
            for (Category category : categories)
            {
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME[1]
                        + " Value(null," + category.title + ")");
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
        }
        finally
        {
            db.endTransaction(); // 结束事务
        }
    }

    /**
     * update deadline's title
     *
     * @param deadLine
     */
    public void updateTitle(DeadLine deadLine)
    {
        Log.d("db", "DBManager --> updateTitle");
        ContentValues cv = new ContentValues();
        cv.put("title", deadLine.title);
        db.update(DatabaseHelper.TABLE_NAME[0], cv, "_id = ?",
                new String[]{Integer.toString(deadLine._id)});
    }

    /**
     * update deadline's isDone
     *
     * @param deadLine
     */
    public void update(DeadLine deadLine)
    {
        Log.d("db", "DBManager --> updateIsDone");

        db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_NAME[0]
                + " VALUES(null,?, ?, ?, ?, ?, ?,0)", new Object[] { deadLine.title,deadLine.category,
                deadLine.datetime, deadLine.settime, deadLine.alarmtime, deadLine.info });
        // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
        // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
        // 使用占位符有效区分了这种情况

        ContentValues cv = new ContentValues();
        cv.put("isDone", deadLine.isDone);
        cv.put("title",deadLine.title);
        cv.put("category",deadLine.category);
        cv.put("datetime",deadLine.datetime);
        cv.put("settime",deadLine.settime);
        cv.put("alarmtime",deadLine.alarmtime);
        cv.put("info",deadLine.info);

        db.update(DatabaseHelper.TABLE_NAME[0], cv, "_id = ?",
                new String[]{Integer.toString(deadLine._id)});
    }

    /**
     * delete old deadlines
     *
     * @param deadLine
     */
    public void deleteDDL(DeadLine deadLine)
    {
        Log.d("db", "DBManager --> deleteDDL");
        db.delete(DatabaseHelper.TABLE_NAME[0], "_id = ?",
                new String[]{Integer.toString(deadLine._id)});
    }

    /**
     * delete old category
     *
     * @param category
     */
    public void deleteCategory(Category category)
    {
        Log.d("db", "DBManager --> deleteCat");
        db.delete(DatabaseHelper.TABLE_NAME[1], "title = ?",
                new String[]{category.title});
    }

    /**
     * query all DeadLines, return list
     *
     * @return List<DeadLines>
     */
    public ArrayList<DeadLine> queryDDL()
    {
        Log.d("db", "DBManager --> queryDDL");
        ArrayList<DeadLine> deadLines = new ArrayList<DeadLine>();
        Cursor c = queryTheCursor0();
        while (c.moveToNext())
        {
            DeadLine deadLine = new DeadLine();
            deadLine._id = c.getInt(c.getColumnIndex("_id"));
            deadLine.title = c.getString(c.getColumnIndex("title"));
            deadLine.category = c.getString(c.getColumnIndex("category"));
            deadLine.datetime = c.getLong(c.getColumnIndex("datetime"));
            deadLine.settime = c.getLong(c.getColumnIndex("settime"));
            deadLine.alarmtime = c.getLong(c.getColumnIndex("alarmtime"));
            deadLine.info = c.getString(c.getColumnIndex("info"));
            deadLine.isDone = c.getLong(c.getColumnIndex("isDone"));
            Date date = new Date(deadLine.datetime);
            deadLine.datetimestr = date.toString();
            date = new Date(deadLine.settime);
            deadLine.settimestr = date.toString();

            deadLines.add(deadLine);
        }
        c.close();
        return deadLines;
    }


    /**
     * query all deadlines, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor0()
    {
        Log.d("db", "DBManager --> queryTheCursor0");

        Date writeTime = new Date();
        long time = writeTime.getTime();

        Cursor c = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE_NAME[0] +" WHERE isDone=0 and datetime > "+ Long.toString(time) + " ORDER BY datetime ASC ",
               null);
        return c;
    }


    /**
     * query all categories, return list
     *
     * @return List<Category>
     */
    public ArrayList<Category> queryCat()
    {
        Log.d("db", "DBManager --> queryCat");
        ArrayList<Category> categories = new ArrayList<Category>();
        Cursor c = queryTheCursor1();
        while (c.moveToNext())
        {
            Category category = new Category();
            category._id = c.getInt(c.getColumnIndex("_id"));
            category.title = c.getString(c.getColumnIndex("title"));
            categories.add(category);
        }
        c.close();
        return categories;
    }

    public ArrayList<DeadLine> queryDDLDone()
    {
        Log.d("db", "DBManager --> queryDDLDone");
        ArrayList<DeadLine> deadLines = new ArrayList<DeadLine>();
        Cursor c = queryTheCursor1();
        while (c.moveToNext())
        {
            DeadLine deadLine = new DeadLine();
            deadLine._id = c.getInt(c.getColumnIndex("_id"));
            deadLine.title = c.getString(c.getColumnIndex("title"));
            deadLine.category = c.getString(c.getColumnIndex("category"));
            deadLine.datetime = c.getLong(c.getColumnIndex("datetime"));
            deadLine.settime = c.getLong(c.getColumnIndex("settime"));
            deadLine.alarmtime = c.getLong(c.getColumnIndex("alarmtime"));
            deadLine.info = c.getString(c.getColumnIndex("info"));
            deadLine.isDone = c.getLong(c.getColumnIndex("isDone"));
            Date date = new Date(deadLine.datetime);
            deadLine.datetimestr = date.toString();
            date = new Date(deadLine.settime);
            deadLine.settimestr = date.toString();

            deadLines.add(deadLine);
        }
        c.close();
        return deadLines;
    }
    /**
     * query all categories, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor1()
    {
        Log.d("db", "DBManager --> queryTheCursor0");

        Date writeTime = new Date();
        long time = writeTime.getTime();

        Cursor c = db.rawQuery("SELECT * FROM "+ DatabaseHelper.TABLE_NAME[0] +" WHERE isDone > 0 ORDER BY isDone ASC ",
                null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB()
    {
        Log.d("db", "DBManager --> closeDB");
        // 释放数据库资源
        db.close();
    }

}