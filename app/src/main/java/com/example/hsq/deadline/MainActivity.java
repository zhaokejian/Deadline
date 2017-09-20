package com.example.hsq.deadline;

import com.example.hsq.deadline.db.DBManager;
import com.example.hsq.deadline.db.DeadLine;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity  {
    private final static int DRAWLENGTH = 70,SINGLEDAY = 40, LEFTMARGIN = 35, TOPMARGIN = 80;
    private final static int REQUEST_CODE = 1;
    private int drawlength;
    private FloatingActionButton button1,button2;
    private DBManager dbManager;
    private  ArrayList<DeadLine> deadLines;
    private DrawView drawView;
    private BubbleLayout bubbleView;
    private ListView DateListView;
    private ArrayList<Mydate> mydateArrayList = new ArrayList<>();
    private ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
    private int status = -1;
    private ScrollView scrollView;
    private AnimationDrawable anim;
    private RelativeLayout relativeLayout,relativeLayoutBling;
    private  int MaxInterval=0;
    private ActionBar actionBar;
    @Override
    protected void onDestroy(){
        super.onDestroy();
        dbManager.closeDB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbManager = new DBManager(this);
        deadLines = dbManager.queryDDL();
        if(deadLines.size()>0) {
            long datetime = deadLines.get(deadLines.size() - 1).datetime;
            MaxInterval = (int) ((datetime - System.currentTimeMillis()) / (24 * 60 * 60 * 1000));
        }
        drawlength = Dp2Px(this,DRAWLENGTH);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        relativeLayoutBling = (RelativeLayout) findViewById(R.id.relativebling);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        DateListView = (ListView) findViewById(R.id.datelistview);

        FloatingActionsMenu rightLabels = (FloatingActionsMenu) findViewById(R.id.right_labels);
        rightLabels.bringToFront();

        //按钮的初始化
        button1 = (FloatingActionButton)findViewById(R.id.normal_plus);
       // button1.bringToFront();
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, addDDL.class);
                //  startActivity(intent);
                //  finish();
                startActivityForResult(intent, REQUEST_CODE);

            }
        });
        button2 = (FloatingActionButton)findViewById(R.id.toddllist);
        // button1.bringToFront();
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, DDLlist.class);
                  startActivity(intent);
                  finish();
//                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        //DDL模块的刷新
        DDLInit(this);
        //提醒功能
        setAlarm();


        //

        ViewGroup.LayoutParams p = DateListView.getLayoutParams();
        p.height = Dp2Px(this,  + (MaxInterval+20) * SINGLEDAY);

        DateListView.setLayoutParams(p);
        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.dateitem, new String[]{"week","date"}, new int[]{R.id.week,R.id.date});
        DateListView.setAdapter(adapter);

        TextView todayM = (TextView) findViewById(R.id.todaymonth);
        TextView todayW = (TextView) findViewById(R.id.todayweek);
        TextView todayD = (TextView) findViewById(R.id.todaydate);

        Calendar cal = Calendar.getInstance();
        int date = cal.get(Calendar.DAY_OF_MONTH);
        String week,month;
        switch (cal.get(Calendar.DAY_OF_WEEK)){
            case 1: week = "Sun";break;
            case 2: week = "Mon";break;
            case 3: week = "Tue";break;
            case 4: week = "Wed";break;
            case 5: week = "Thu";break;
            case 6: week = "Fri";break;
            case 7: week = "Sat";break;
            default:week = "";break;
        }
        switch (cal.get(Calendar.MONTH)+1){
            case 1: month = "Jan";break;
            case 2: month = "Feb";break;
            case 3: month = "Mar";break;
            case 4: month = "Apr";break;
            case 5: month = "May";break;
            case 6: month = "Jun";break;
            case 7: month = "July";break;
            case 8: month = "Aug";break;
            case 9: month = "Sept";break;
            case 10: month = "Oct";break;
            case 11: month = "Nov";break;
            case 12: month = "Dec";break;
            default:month = "";break;
        }
        todayM.setText(month);
        todayW.setText(week);
        todayD.setText(Integer.toString(date));

        final Context context = this;
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ddlLayout);
        int cntChild = linearLayout.getChildCount()-1;
        for(int i = 0; i < cntChild; i++){
            DrawView d = (DrawView)linearLayout.getChildAt(i+1);
            final int finalI = i;
            d.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (status != -1) {
                        relativeLayout.getChildAt(status).setVisibility(View.GONE);
                        status = -1;
                    }
                    else {
                        relativeLayout.getChildAt(finalI + 1).setVisibility(View.VISIBLE);
                        DeadLine deadLine =  ((BubbleLayout)relativeLayout.getChildAt(finalI + 1)).deadLine;
                        long datetime = deadLine.datetime;
                        int interval = (int)( (datetime - System.currentTimeMillis())/(24*60*60*1000));
                        final float scrollY = Dp2Px(context,SINGLEDAY)*interval-500;
                        //scrollView.scrollTo(0,singleday*interval-500);
                        // 这个timer执行1000毫秒 每20毫秒回调一次
                        new CountDownTimer(1000, 20) {
                            public void onTick(long millisUntilFinished) {
                                float y = scrollView.getScrollY();
                                scrollView.scrollTo(0,(int)(y + millisUntilFinished*(scrollY-y)/1000)); //更新位置

                            }
                            public void onFinish() {
                               //scrollView.scrollTo(0,scrollY);  //可能会存在不精确的情况  校准一下
                            }
                        }.start();
                        status = finalI + 1;
                    }
                }
            });
        }


    }

    private void DDLInit(final Context context){
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ddlLayout);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                drawlength, LinearLayout.LayoutParams.MATCH_PARENT);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        LinearLayout l = new LinearLayout(context);
        linearLayout.addView(l, new LinearLayout.LayoutParams(Dp2Px(context, 30), LinearLayout.LayoutParams.MATCH_PARENT));
        for (final DeadLine deadLine : deadLines){
            //画线view的生成

            drawView = new DrawView(this,deadLine);
            drawView.invalidate();
            linearLayout.addView(drawView, p);
            int i = linearLayout.getChildCount()-1;
            final int finalI = i;


            //bubbleview的生成
            RelativeLayout.LayoutParams p0 = new RelativeLayout.LayoutParams(
                    Dp2Px(context,200),ViewGroup.LayoutParams.WRAP_CONTENT );

            long settime = deadLine.settime;
            final long datetime = deadLine.datetime;
            final int interval = (int)( (datetime - System.currentTimeMillis())/(24*60*60*1000));
            int singleday = Dp2Px(context,SINGLEDAY);
            bubbleView = new BubbleLayout(this, deadLine);
            bubbleView.bringToFront();
            if(interval>4) {
                p0.topMargin = singleday * interval - Dp2Px(context,95);
                p0.leftMargin = drawlength * (linearLayout.getChildCount() - 2)+Dp2Px(context,8);
                bubbleView.setBackgroundResource(R.drawable.bubble);
            }
            else {
                p0.topMargin = singleday * interval+ Dp2Px(context,24)+Dp2Px(context,TOPMARGIN);
                p0.leftMargin = drawlength * (linearLayout.getChildCount() - 2)+Dp2Px(context,6);
                bubbleView.setBackgroundResource(R.drawable.bubble1);
            }

            //bubble里完成按钮的监听
            bubbleView.button1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    deadLine.isDone = System.currentTimeMillis();
                    dbManager.update(deadLine);
                    relativeLayout.getChildAt(finalI).setVisibility(View.GONE);
                    linearLayout.getChildAt(finalI).setVisibility(View.GONE);
                    if(interval<2)
                        relativeLayoutBling.getChildAt(finalI).setVisibility(View.GONE);

                    int cnt = linearLayout.getChildCount()-1;
                    for (int i = finalI + 1; i <= cnt; i++) {
                        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) relativeLayout.getChildAt(i).getLayoutParams();
                        p.leftMargin -= drawlength;
                        relativeLayout.getChildAt(i).setLayoutParams(p);
                        if(interval<2){
                            if(i<=relativeLayoutBling.getChildCount()){
                                RelativeLayout.LayoutParams p1 = (RelativeLayout.LayoutParams) relativeLayoutBling.getChildAt(i - 1).getLayoutParams();
                                p1.leftMargin -= drawlength;
                                relativeLayoutBling.getChildAt(i - 1).setLayoutParams(p1);
                            }
                        }

                    }

                }
            });
            bubbleView.button2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbManager.deleteDDL(deadLine);
                    relativeLayout.getChildAt(finalI).setVisibility(View.GONE);
                    linearLayout.getChildAt(finalI).setVisibility(View.GONE);
                    if(interval<2)
                        relativeLayoutBling.getChildAt(finalI).setVisibility(View.GONE);

                    int cnt = linearLayout.getChildCount()-1;
                    for (int i = finalI + 1; i <= cnt; i++) {
                        RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) relativeLayout.getChildAt(i).getLayoutParams();
                        p.leftMargin -= drawlength;
                        relativeLayout.getChildAt(i).setLayoutParams(p);
                        if(interval<2){
                            if(i<=relativeLayoutBling.getChildCount()){
                                RelativeLayout.LayoutParams p1 = (RelativeLayout.LayoutParams) relativeLayoutBling.getChildAt(i - 1).getLayoutParams();
                                p1.leftMargin -= drawlength;
                                relativeLayoutBling.getChildAt(i - 1).setLayoutParams(p1);
                            }
                        }

                    }
                }

            });
            bubbleView.button3.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, addDDL.class);
                    //  startActivity(intent);
                    //  finish();
                    startActivityForResult(intent,REQUEST_CODE);
                }
            });

            relativeLayout.addView(bubbleView, p0);
            relativeLayout.getChildAt(relativeLayout.getChildCount()-1).setVisibility(View.GONE);


            //闪烁的生成
            if(interval<2) {
                ImageView imageView = new ImageView(this);
                imageView.setBackgroundResource(R.drawable.blingbling);
                anim = (AnimationDrawable) imageView.getBackground();
                anim.start();

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        Dp2Px(context,45),Dp2Px(context,45));
                params.setMargins(Dp2Px(this, (i-1) *DRAWLENGTH + LEFTMARGIN +7), Dp2Px(this, interval * SINGLEDAY + TOPMARGIN ), 0, 0);


                imageView.setLayoutParams(params);
                params.addRule(RelativeLayout.ALIGN_TOP, RelativeLayout.TRUE);
                relativeLayoutBling.addView(imageView, params);
            }
        }
    }
    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    private ArrayList<HashMap<String,Object>> getData(){
        setDate();
        for(Mydate tmp : mydateArrayList){
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("week", tmp.getW());
            map.put("date", tmp.getD());
            data.add(map);
        }
        return data;
    }

    private ArrayList<Mydate> setDate(){
        Calendar c = Calendar.getInstance();
        for(int i=0;i<MaxInterval+20;i++){
            c.add(Calendar.DATE,1);
            String week;
            switch (c.get(Calendar.DAY_OF_WEEK)){
                case 1: week = "Sun";break;
                case 2: week = "Mon";break;
                case 3: week = "Tue";break;
                case 4: week = "Wed";break;
                case 5: week = "Thu";break;
                case 6: week = "Fri";break;
                case 7: week = "Sat";break;
                default:week = "";break;
            }
            Mydate day = new Mydate( week,(Integer.toString(c.get(Calendar.DAY_OF_MONTH))));
            mydateArrayList.add(day);
        }
        return mydateArrayList;
    }

    private void setAlarm(){
        ArrayList<DeadLine> deadLines = dbManager.queryDDL();
        for(DeadLine deadLine : deadLines){
            if(deadLine.alarmtime > System.currentTimeMillis()) {
                setReminder(deadLine.alarmtime, deadLine._id, deadLine.title);
           //     Log.d("Alarm", deadLine.alarmtimestr);
            }
        }
    }

    private void setReminder(long selectTime, int id, String Info){
        //得到使用AlarmManager的权限
        AlarmManager alarms=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //AlarmManager要做的事情,打开NotificationService(这是个自定义Service稍后会补充)
        Intent intent=new Intent(this,NotificationService.class);
        intent.putExtra("info", Info);
        PendingIntent pendingIntent= PendingIntent.getService(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //设定时间,将System.currentTimeMillis()换成你需要的时间即可,这里是立即触发
        alarms.set(AlarmManager.RTC_WAKEUP, selectTime, pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }


}
