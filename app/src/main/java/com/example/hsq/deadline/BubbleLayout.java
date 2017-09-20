package com.example.hsq.deadline;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.example.hsq.deadline.db.DeadLine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by HSQ on 15/12/18.
 */
public class BubbleLayout extends LinearLayout {

    private ListView listView;
    public ImageButton button1,button2,button3;
    public DeadLine deadLine;

    public BubbleLayout(Context context, DeadLine deadLine) {
        super(context);
        this.setOrientation(LinearLayout.VERTICAL);
        this.deadLine = deadLine;
        listView = new ListView(context);
        SimpleAdapter adapter = new SimpleAdapter(context, getData(deadLine), R.layout.list_item, new String[]{"ico","text"}, new int[]{R.id.DDLimage,R.id.DDLtitle});
        listView.setAdapter(adapter);
        RelativeLayout.LayoutParams p0 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT );
        p0.addRule(RelativeLayout.ALIGN_LEFT);
        p0.addRule(RelativeLayout.ALIGN_TOP);
        this.addView(listView, p0);
        LinearLayout line = new LinearLayout(context);
        line.setBackgroundColor(getResources().getColor(R.color.divider));
        line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Dp2Px(context, 1)));
        this.addView(line);

        RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.buttonsinbubble, null);
        button1 = (ImageButton) relativeLayout.getChildAt(0);
        button2 = (ImageButton) relativeLayout.getChildAt(1);
        button3 = (ImageButton) relativeLayout.getChildAt(2);
        button1.setBackgroundResource(R.drawable.done1);
        button2.setBackgroundResource(R.drawable.delete1);
        button3.setBackgroundResource(R.drawable.edit1);
        this.addView(relativeLayout);

    }
    private ArrayList<HashMap<String,Object>> getData(DeadLine deadLine){
        ArrayList<HashMap<String,Object>>  data = new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> map1 = new HashMap<String,Object>();
        HashMap<String,Object> map2 = new HashMap<String,Object>();
        HashMap<String,Object> map3 = new HashMap<String,Object>();
        map1.put("text",deadLine.title);
        map1.put("ico", R.drawable.flag);
        data.add(map1);

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(deadLine.datetime);
        Formatter ft1=new Formatter();
        Formatter ft2=new Formatter();
        map2.put("text", ft1.format("%1$tY年%1$tm月%1$td日", cal1).toString());
        map2.put("ico", R.drawable.calendar);
        data.add(map2);
        cal2.setTimeInMillis(deadLine.settime);
        map3.put("text", ft2.format("%1$tY年%1$tm月%1$td日", cal2).toString());
        map3.put("ico", R.drawable.alarm);
        data.add(map3);

        return data;
    }
    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}

