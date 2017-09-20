package com.example.hsq.deadline;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.hsq.deadline.db.DBManager;
import com.example.hsq.deadline.db.DeadLine;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

/**
 * Created by kejian on 2015/12/21.
 */
public class DDLlist extends Activity {
    private DBManager dbManager;
    private ArrayList<DeadLine> mDDLlist;
    private DDLAdapter mAdapter;
    private SwipeMenuListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ddllist);
        dbManager = new DBManager(getBaseContext());
//        mAppList = getPackageManager().getInstalledApplications(0);
        mDDLlist = dbManager.queryDDLDone();

        mListView = (SwipeMenuListView) findViewById(R.id.ddllist);
        mAdapter = new DDLAdapter();
        mListView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "undo" item
                SwipeMenuItem undoItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                undoItem.setBackground(new ColorDrawable(Color.rgb(0xf9, 0xC6,
                        0x18)));
                // set item width
                undoItem.setWidth(dp2px(50));
                undoItem.setIcon(R.drawable.undo);
                // set item title
//                undoItem.setTitle("undo");
                // set item title fontsize
//                undoItem.setTitleSize(18);
                // set item title font color
//                undoItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(undoItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(50));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
//                ApplicationInfo item = mAppList.get(position);
                DeadLine item = mDDLlist.get(position);
                switch (index) {
                    case 0:
                        // undo
                        undo(item);
                        mDDLlist.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        // delete
                        delete(item);
                        mDDLlist.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        // set SwipeListener
        mListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

    }

    private void delete(DeadLine item) {
        // delete DDL
        dbManager.deleteDDL(item);
    }

    private void undo(DeadLine item) {
        // undo DDL
        item.isDone = 0;
        dbManager.update(item);
    }

    class DDLAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDDLlist.size();
        }

        @Override
        public DeadLine getItem(int position) {
            return mDDLlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }//???????????????????????????????????????????????????????????????????????????????????????

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.ddl_card, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            DeadLine item = getItem(position);

            long donetime = item.isDone;
            long ddltime = item.datetime;
            int interval = (int)((ddltime - donetime)/(24*60*60*1000));

            if(interval<=3)
                holder.icon_circle.setImageDrawable(getDrawable(R.drawable.red));
            else if(interval>3 && interval <=7)
                holder.icon_circle.setImageDrawable(getDrawable(R.drawable.yellow));
            else
                holder.icon_circle.setImageDrawable(getDrawable(R.drawable.green));

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(item.datetime);
            cal2.setTimeInMillis(item.isDone);
            Formatter ft1=new Formatter();
            Formatter ft2=new Formatter();
            holder.title.setText(item.title);
            holder.ddl.setText(ft1.format("%1$tY年%1$tm月%1$td日", cal1).toString());
            holder.done.setText(ft2.format("%1$tY年%1$tm月%1$td日", cal2).toString());
            return convertView;
        }

        class ViewHolder {
            ImageView icon_circle;
            TextView title;
            TextView ddl;
            TextView done;

            public ViewHolder(View view) {
                icon_circle = (ImageView) view.findViewById(R.id.circle);
                title = (TextView) view.findViewById(R.id.texttitle);
                ddl = (TextView) view.findViewById(R.id.textddl);
                done = (TextView) view.findViewById(R.id.textdone);
                view.setTag(this);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
