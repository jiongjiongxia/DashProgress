package com.yangdong.dashprogress;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by yangdong on 2017/12/13.
 */

public class DashProgressCircle extends View {



    public String TAG = getClass().getName();


    private int padding = 10;//
    private int dashSpace = 5;//
    private int uit = 5;//
    private int internalStrokeWidth = 30;
    private int bgclolor = 0xff4c505d;//
    private int unitbgcolor = 0xff343740;//
    private int unitmidcolor = 0xffdfea5c;//
    private int unitstartcolor = 0xff56ebd1;//
    private int unitendcolor = 0xfff9627a;
    private int duration = 500;
    private boolean dash = true;
    private int roate = 270;

    private Paint startPaint;
    private Paint endPaint;
    private RectF recthead ;

    private Paint outpaint;
    private Paint bgpaint;
    private float pre = 0f;
    private int perprogress = 0;
    SweepGradient sweepGradient;

    public int getProgress(){
        return perprogress;
    }

    public void setProgress(int progress){
        if(perprogress==progress)return;
        if(progress>100)progress=100;
        perprogress = progress;
        pre = progress/(100*1.0f);
        postInvalidate();
    }

    public void setDash(boolean dash){
        this.dash = dash;
        init();
        postInvalidate();
    }

    ObjectAnimator animator;
    public void setSmoothProgress(int progress){
        if(animator!=null)
            animator.cancel();
        animator = ObjectAnimator.ofInt(this,"Progress",getProgress(),progress);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    public DashProgressCircle(Context context) {
        super(context);
        init();
    }

    public DashProgressCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseAttr(context,attrs);
        init();
    }

    public DashProgressCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttr(context,attrs);
        init();
    }

    private void parseAttr(final Context context, final AttributeSet attrs){
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashProgressView);
            internalStrokeWidth = (int)typedArray.getDimension(R.styleable.DashProgressView_dashinnerstokewidth, 30);
            padding = (int)typedArray.getDimension(R.styleable.DashProgressView_dashpadding,10);
            dashSpace = (int)typedArray.getDimension(R.styleable.DashProgressView_dashspace,5);
            uit = (int)typedArray.getDimension(R.styleable.DashProgressView_dashwidth,5);
            bgclolor = typedArray.getColor(R.styleable.DashProgressView_dashbgcolor,0xff4c505d);
            unitbgcolor = typedArray.getColor(R.styleable.DashProgressView_dashunitbgcolor,0xff343740);
            unitmidcolor = typedArray.getColor(R.styleable.DashProgressView_dashmidcolor,0xffdfea5c);
            unitstartcolor = typedArray.getColor(R.styleable.DashProgressView_dashstartcolor,0xff56ebd1);
            unitendcolor = typedArray.getColor(R.styleable.DashProgressView_dashendcolor,0xfff9627a);
            duration = typedArray.getInt(R.styleable.DashProgressView_dashsmoothtime,500);
            dash = typedArray.getBoolean(R.styleable.DashProgressView_dashstyle,true);
            roate = typedArray.getInt(R.styleable.DashProgressView_dashroate,270);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    private void init(){
        calculateDelta();

        bgpaint=new Paint();
        bgpaint.setAntiAlias(true);//抗锯齿功能
        bgpaint.setColor(bgclolor);  //设置画笔颜色
        bgpaint.setStyle(Paint.Style.FILL);//设置填充样式   Style.FILL/Style.FILL_AND_STROKE/Style.STROKE

        setLayerType( LAYER_TYPE_SOFTWARE , null);
        outpaint = new Paint();
        outpaint.setAntiAlias(true);
        outpaint.setStyle(Paint.Style.STROKE);
        outpaint.setStrokeWidth(internalStrokeWidth);
        if(dash) {
            outpaint.setPathEffect(new DashPathEffect(new float[]{uit, dashSpace},
                    0));
        }else{
            //设置线冒，这个并非不管用，而是他的颜色未达到预期
//            outpaint.setStrokeJoin(Paint.Join.ROUND);
//            outpaint.setStrokeCap(Paint.Cap.ROUND);
            //伪装的线冒哈哈
            startPaint = new Paint();
            startPaint.setColor(unitstartcolor);
            startPaint.setAntiAlias(true);
            startPaint.setStyle(Paint.Style.FILL);


            endPaint = new Paint();
            endPaint.setAntiAlias(true);
            endPaint.setStyle(Paint.Style.FILL);


        }


    }





    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int store = canvas.save();
        //参数修正
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();




        //圆心
        int x = w/2;
        int y = h/2;
        //半径
        int radius = (Math.min(w,h)-2*padding)/2-internalStrokeWidth/2;//半径
        //线冒的范围,默认在roate 为0的位置，之后通过旋转摆正
        recthead = new RectF(x+radius-internalStrokeWidth/2,y-internalStrokeWidth/2,
                x+radius+internalStrokeWidth/2,y+internalStrokeWidth/2);

        calculatePercentEndColor(pre);
        sweepGradient = new SweepGradient(x,y,colors,dur);
        //旋转sweepGradient的角度
        Matrix gradientMatrix = new Matrix();
        gradientMatrix.preRotate(roate, x, y);
        sweepGradient.setLocalMatrix(gradientMatrix);

        outpaint.setShader(sweepGradient);


        //绘制背景
        canvas.drawCircle(x,y,radius+internalStrokeWidth/2+padding,bgpaint);

        //绘制外部单元格
        canvas.drawCircle(x,y,radius,outpaint);

        //如果是非dash样式，线冒会被染色为终点颜色.例如红色，这里需要一个半圆盖着
        if(!dash){
            //为保证旋转不画布其他元素生效
            if(pre>0&&pre<1) {
                canvas.save();
                // 绘制开头的半圆，线冒
                canvas.rotate(roate, x, y);
                canvas.drawArc(recthead, 180, 180, true, startPaint);
                canvas.restore();
            }
            // 绘制结束的半圆，线冒
            if (pre>0&&pre <= 1) {
                //为保证旋转不画布其他元素生效
                canvas.save();
                endPaint.setColor(percentEndColor);
                //-1个角度，因为计算后有一定的精度损失
                canvas.rotate(roate+360*pre-1, x, y);
                canvas.drawArc(recthead, 0f, 180f, true, endPaint);
                canvas.restore();
            }


        }

        canvas.restoreToCount(store);

    }

    //当前进度条的最终颜色
    private int percentEndColor;
    private int startR, startB, startG;
    private int midR, midB, midG;
    private int deltaR1, deltaB1, deltaG1;//前半段颜色差，即绿色到黄色
    private int deltaR2, deltaB2, deltaG2;//后半段颜色差，即黄色到黄红

    private void calculateDelta() {
         midR = (unitmidcolor & 0xFF0000) >> 16;
         midG = (unitmidcolor & 0xFF00) >> 8;
         midB = (unitmidcolor & 0xFF);

        int endR = (unitendcolor & 0xFF0000) >> 16;
        int endG = (unitendcolor & 0xFF00) >> 8;
        int endB = (unitendcolor & 0xFF);

        this.startR = (unitstartcolor & 0xFF0000) >> 16;
        this.startG = (unitstartcolor & 0xFF00) >> 8;
        this.startB = (unitstartcolor & 0xFF);

        deltaR1 = midR - startR;
        deltaG1 = midG - startG;
        deltaB1 = midB - startB;

        deltaR2 = endR - midR;
        deltaG2 = endG - midG;
        deltaB2 = endB - midB;
    }

    int colors[];//进度条的颜色序列
    float dur[];//进度条的比例序列
    private synchronized void calculatePercentEndColor(final float percent) {

        //默认3个颜色点，分别是开始，中间0.45f 以及终点
        if(percent<0.45f){
            float fixper = percent/0.45f;
            percentEndColor = ((int) (deltaR1 * fixper + startR) << 16) +
                    ((int) (deltaG1 * fixper + startG) << 8) +
                    ((int) (deltaB1 * fixper + startB)) + 0xFF000000;
            colors = new int[]{unitstartcolor,percentEndColor,unitbgcolor,unitbgcolor};
            //这个值是按角度计算的,1.0就是360度位置
            dur = new float[]{0f,percent,percent,1.0f};
        }else {
            float fixper = (percent-0.45f)/0.55f;
            percentEndColor = ((int) (deltaR2 * fixper + midR) << 16) +
                    ((int) (deltaG2 * fixper + midG) << 8) +
                    ((int) (deltaB2 * fixper + midB)) + 0xFF000000;
            colors = new int[]{unitstartcolor,unitmidcolor,percentEndColor,unitbgcolor,unitbgcolor};
            //这个值是按角度计算的,1.0就是360度位置
            dur = new float[]{0f,0.45f,percent,percent,1.0f};
        }

    }

}
