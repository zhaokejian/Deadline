package com.example.hsq.deadline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.hsq.deadline.db.DeadLine;

import java.util.jar.Attributes;

/**
 * Created by HSQ on 15/12/17.
 */
public class DrawView extends View implements OnClickListener {
    //定义画布、画笔等绘图对象
    private final static int LEFTMARGIN = 35,TOPMARGIN = 80, SINGLEDAY = 40,TOTAL_PAINT_TIMES=30;
    private Paint paint;
    private DeadLine deadLine;
    private int status = 0;
    int leftmargin,topmargin,singleday;
    public DeadLine getDeadline(){
        return deadLine;
    }
    private int mPaintTimes = 0;


    public DrawView(Context context,DeadLine deadLine)
    {
        super(context);
        this.deadLine = deadLine;
        //定义一个bitmap用作画布背景
        paint=new Paint(Paint.DITHER_FLAG);
        leftmargin = Dp2Px(context,LEFTMARGIN);
        topmargin = Dp2Px(context,TOPMARGIN);
        singleday = Dp2Px(context,SINGLEDAY);
    }


    @Override
    protected void onDraw(final Canvas canvas)
    {
        super.onDraw(canvas);
        mPaintTimes ++ ;

        paint.setAntiAlias(true);
        paint.setStrokeWidth((float) Dp2Px(getContext(),5));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(getResources().getColor(R.color.divider));
        canvas.drawLine(leftmargin, 0, leftmargin, topmargin, paint);// 画线

        long settime = deadLine.settime;
        long datetime = deadLine.datetime;
        final int interval = (int)((datetime - System.currentTimeMillis())/(24*60*60*1000));

        if(interval<2)
            paint.setColor(getResources().getColor(R.color.ddlRed));
        else if(interval>=2 && interval <5)
            paint.setColor(getResources().getColor(R.color.ddlYello));
        else
            paint.setColor(getResources().getColor(R.color.ddlGreen));

       // final long drawtimeInmills = interval;
//        new CountDownTimer(1000, 20) {
//            public void onTick(long millisUntilFinished) {
//                canvas.drawLine(leftmargin, topmargin+5, leftmargin, topmargin+5+(float)(interval*singleday)/(float)1000 * (1000 - millisUntilFinished), paint);// 画线
//                canvas.drawCircle(leftmargin, topmargin+5+(float)(interval*singleday)/(float)1000 * (1000 - millisUntilFinished), 25, paint);
//            }
//            public void onFinish() {
//
//            }
//        }.start();
        canvas.drawLine(leftmargin, topmargin + 5, leftmargin, topmargin + 5 + (Dp2Px(getContext(),20)+interval * singleday)/(float)TOTAL_PAINT_TIMES*mPaintTimes, paint);// 画线
        canvas.drawCircle(leftmargin, topmargin+5+(Dp2Px(getContext(),20)+interval*singleday)/(float)TOTAL_PAINT_TIMES*mPaintTimes, Dp2Px(getContext(),12), paint);

        if( mPaintTimes < TOTAL_PAINT_TIMES ) {
            invalidate(); //实现动画的关键点
        }
    }

    @Override
    public void onClick(View v) {
        if(status == 0)
            status = 1;
        else status = 0;
    }
    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public int Px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
    public void setStatus(){
        status = 0;
    }

}
