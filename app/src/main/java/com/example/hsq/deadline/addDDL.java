package com.example.hsq.deadline;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.example.hsq.deadline.db.DBManager;
import com.example.hsq.deadline.db.DeadLine;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by HSQ on 15/12/16.
 */
public class addDDL extends Activity {
    private TimePicker timePicker;
    private FloatingActionButton button;
    /*
        private DatePicker datePicker;
    */
    private EditText editText;

    private EditText ddldate;
    private EditText alarmdate;
    private EditText alarmtime;
    //    private int flagddl=0;
    private int flagalarm=0;
    private int flagalarmtime=0;

    private DBManager dbManager;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_addddl);
        dbManager = new DBManager(this);
/*
        datePicker = (DatePicker)findViewById(R.id.datePicker);
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int monthOfYear=calendar.get(Calendar.MONTH);
        int dayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, monthOfYear, dayOfMonth, new OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.d("addDDL","datePickerChanged");
            }
        });

        //timePicker = (TimePicker)findViewById(R.id.timePicker);
  */


        editText = (EditText) findViewById(R.id.addDDLtext);
/**/    ddldate=(EditText)findViewById(R.id.DDLdatetext);
        alarmdate=(EditText)findViewById(R.id.alarmdatetext);
        alarmtime=(EditText)findViewById(R.id.alarmtimetext);
        //控制登录图标大小
        Drawable drawable1 = getResources().getDrawable(R.drawable.flag);
        Drawable drawable2 = getResources().getDrawable(R.drawable.calendar);
        Drawable drawable3 = getResources().getDrawable(R.drawable.alarm);
        Drawable drawable4 = getResources().getDrawable(R.drawable.blank);

        drawable1.setBounds(0, 0, 60, 60);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        drawable2.setBounds(0, 0, 60, 60);
        drawable3.setBounds(0, 0, 60, 60);
        drawable4.setBounds(0, 0, 60, 60);

        editText.setCompoundDrawables(drawable1, null, null, null);//只放左边
        ddldate.setCompoundDrawables(drawable2, null, null, null);
        alarmdate.setCompoundDrawables(drawable3, null, null, null);
        alarmtime.setCompoundDrawables(drawable4, null, null, null);
//        ddldate.setInputType(InputType.TYPE_NULL);
//        alarmdate.setInputType(InputType.TYPE_NULL);
//        alarmtime.setInputType(InputType.TYPE_NULL);

/**/

        editText.clearFocus();

/**/    //ddldate
        final Calendar c1 = Calendar.getInstance();
        ddldate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.clearFocus();
                DatePickerDialog dialog = new DatePickerDialog(addDDL.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c1.set(year, monthOfYear, dayOfMonth);
                        ddldate.setText(DateFormat.format("MM月dd日", c1));
//                        flagddl=1;
                    }
                }, c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH));
                dialog.show();

            }
        });

        //alarmdate
        final Calendar c2 = Calendar.getInstance();
        alarmdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.clearFocus();
                DatePickerDialog dialog2 = new DatePickerDialog(addDDL.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c2.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        c2.set(Calendar.HOUR_OF_DAY,9);
                        c2.set(Calendar.MINUTE, 0);
                        flagalarm=1;
                        alarmdate.setText(DateFormat.format("MM月dd日", c2));
                    }
                }, c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH));
                dialog2.show();

            }
        });

        //点击时间按钮布局 设置时间
        //alarmtime
//        int hour,minute;
        if(flagalarm==1) alarmdate.setText("09:00");
        alarmtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.clearFocus();
                TimePickerDialog dialog3 = new TimePickerDialog(addDDL.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        c2.set(Calendar.HOUR_OF_DAY,hour);
                        c2.set(Calendar.MINUTE, minute);
                        alarmtime.setText(DateFormat.format("HH:mm",c2));
//                        flagalarmtime=1;
                    }
                }, c2.get(Calendar.HOUR_OF_DAY), c2.get(Calendar.MINUTE),true);
                dialog3.show();

            }
        });


/**/

        button = (FloatingActionButton)findViewById(R.id.addDDLDone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取datePicker的时间并转换成需要的格式
//                int y = datePicker.getYear();
//                int m = datePicker.getMonth();
//                int d = datePicker.getDayOfMonth();
//                Calendar ca = Calendar.getInstance();
//                ca.set(y, m, d, 0, 0, 0);
                Date date = c1.getTime();
                long DDLdatetime = date.getTime();

                long DDLsettime = new Date().getTime();//获取当前时间为ddl设置时间

                String DDLTitle = editText.getText().toString();
/**/
                DeadLine deadLine;
                if(flagalarm==0){
                    deadLine = new DeadLine(DDLTitle,null,DDLdatetime,DDLsettime,0,null,0);
                }
                else {
                    deadLine = new DeadLine(DDLTitle,null,DDLdatetime,DDLsettime,c2.getTimeInMillis(),null,0);
                    Log.d("db->","addalarmtime "+deadLine.alarmtimestr+" /"+c2.getTime()+" /"+deadLine.alarmtime);
                }
/**/
                ArrayList<DeadLine> list = new ArrayList<DeadLine>();
                list.add(deadLine);
                dbManager.addDDL(list);
                Intent intent = new Intent(addDDL.this,MainActivity.class);
                startActivity(intent);
                addDDL.this.setResult(1);
                addDDL.this.finish();
            }
        });
    }

}
