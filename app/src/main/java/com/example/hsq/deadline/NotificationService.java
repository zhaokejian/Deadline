package com.example.hsq.deadline;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


/**
 * Created by kejian on 2015/12/18.
 */
public class NotificationService extends Service{
    /**
     * 继承Service必须实现的方法,这里用不到
     */
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    /**
     * 初始化
     */
    public void onCreate()
    {
        super.onCreate();
        Log.e("NotificationService","onCreate");
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String Info=intent.getStringExtra("info");
        Notification(Info);
        return START_REDELIVER_INTENT;
    }
    private void Notification(String Info){
        //以下是对Notification的各种参数设定
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        //通过Intent，使得点击Notification之后会启动新的Activity
        Intent i = new Intent(this, MainActivity.class);
        //该标志位表示如果Intent要启动的Activity在栈顶，则无须创建新的实例
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIndent = PendingIntent.getActivity(this, 0, i, 0);

        builder.setContentIntent(contentIndent)//indent
                .setContentInfo(Info)
                .setSmallIcon(R.drawable.alarm1)//设置状态栏里面的图标（小图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                        //.setLargeIcon(BitmapFactory.decodeResource(R.drawable.logo2))//下拉下拉列表里面的图标（大图标） 　　　　　　　
                        // .setTicker("this is bitch!")// 设置状态栏的显示的信息
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                        //.setWhen(System.currentTimeMillis())//设置时间发生时间
                .setContentTitle("Deadline")//设置下拉列表里的标题
                .setContentText("您有一个DDL到期");//设置上下文内容
        notificationManager.notify((int)System.currentTimeMillis(),builder.build());
    }

}
