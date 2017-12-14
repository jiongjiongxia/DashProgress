package com.yangdong.dashprogress;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by yangdong on 2017/12/13.
 */

public class DashProgressBar extends View {


    public String TAG = getClass().getName();

    private int radius = 10;
    private int padding = 10;//
    private int dashSpace = 5;//
    private int uit = 5;//
    private int bgclolor = 0xff4c505d;//
    private int unitbgcolor = 0xff343740;//
    private int unitmidcolor = 0xffdfea5c;//
    private int unitstartcolor = 0xff56ebd1;//
    private int unitendcolor = 0xfff9627a;
    private int duration = 500;
    private boolean dash = false;
    private boolean direction_leftorbottom = true;


    private Paint outpaint;//外部
    private Path outpath;//外部path
    private Path innerpath;//内部path
    private Paint innerpaint;//内部
    private Paint bgpaint;
    private float pre = 0f;
    private int perprogress = 0;
    LinearGradient linearGradient;

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

    public void setDirection(boolean direction_leftorbottom){
        this.direction_leftorbottom = direction_leftorbottom;
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

    public DashProgressBar(Context context) {
        super(context);
        init();
    }

    public DashProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseAttr(context,attrs);
        init();
    }

    public DashProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttr(context,attrs);
        init();
    }

    private void parseAttr(final Context context, final AttributeSet attrs){
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashProgressView);
            radius = (int)typedArray.getDimension(R.styleable.DashProgressView_dashbgradius, 10);
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
            direction_leftorbottom = typedArray.getBoolean(R.styleable.DashProgressView_dashdircetion_leftbottom,true);
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    private void init(){



        //设置画笔基本属性
        bgpaint=new Paint();
        bgpaint.setAntiAlias(true);//抗锯齿功能
        bgpaint.setColor(bgclolor);  //设置画笔颜色
        bgpaint.setStyle(Paint.Style.FILL);//设置填充样式   Style.FILL/Style.FILL_AND_STROKE/Style.STROKE
        bgpaint.setStrokeWidth(5);//设置画笔宽度


        //底部单元格
        innerpaint = new Paint();
        innerpaint.setAntiAlias(true);
        innerpaint.setColor(unitbgcolor);
        innerpaint.setStyle(Paint.Style.STROKE);
        if(dash)
            innerpaint.setPathEffect(new DashPathEffect(new float[]{uit, dashSpace},
                    0));

        innerpath = new Path();


        //进度单元格
        setLayerType( LAYER_TYPE_SOFTWARE , null);
        outpaint = new Paint();
        outpaint.setAntiAlias(true);
        outpaint.setStyle(Paint.Style.STROKE);
        if(dash)
            outpaint.setPathEffect(new DashPathEffect(new float[]{uit, dashSpace},
                    0));
        //outpaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.SOLID));

        outpath = new Path();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //参数修正
        boolean island = true;

        int pathstartx = padding ;
        int pathstraty = getMeasuredHeight() / 2;
        int pathendx = getMeasuredWidth() - padding ;
        int pathendy = getMeasuredHeight() / 2;
        int internalStrokeWidth = getMeasuredHeight()-2*padding;


        int colors[] = new int[]{unitstartcolor,unitmidcolor,unitendcolor};
        if(!direction_leftorbottom)
            colors = new int[]{unitendcolor,unitmidcolor,unitstartcolor};
        float dur[] = new float[]{0.0f,0.45f,1.0f};
        linearGradient = new LinearGradient(pathstartx,pathstraty,pathendx,pathendy,colors,dur, Shader.TileMode.CLAMP);

        outpaint.setShader(linearGradient);
        //判断控件是竖着绘制还是正着绘制
        if(getMeasuredWidth()>getMeasuredHeight()){
            //横向，进度由左往右
        }else{
            //竖向, 进度由下往上
            island = false;
            pathstartx = pathendx =  getMeasuredWidth()/2;
            pathstraty = getHeight()-padding;
            pathendy = padding;
            internalStrokeWidth = getMeasuredWidth()-2*padding;
            linearGradient = new LinearGradient(pathstartx,pathstraty,pathendx,pathendy,colors,dur, Shader.TileMode.CLAMP);
            outpaint.setShader(linearGradient);
        }

        //绘制背景
        RectF rect = new RectF(0,0,getMeasuredWidth(),getMeasuredHeight());
        canvas.drawRoundRect(rect,radius,radius,bgpaint);

        //绘制背景单元格
        innerpath.reset();
        if(direction_leftorbottom) {
            innerpath.moveTo(pathstartx, pathstraty);
            innerpath.lineTo(pathendx, pathendy);
        }else{
            innerpath.moveTo(pathendx, pathendy);
            innerpath.lineTo(pathstartx, pathstraty);
        }
        innerpaint.setStrokeWidth(internalStrokeWidth);
        canvas.drawPath(innerpath,innerpaint);

        //绘制外部
        outpath.reset();
        if(direction_leftorbottom) {
            outpath.moveTo(pathstartx, pathstraty);
            outpath.lineTo(island ? ((getMeasuredWidth() - 2 * padding) * pre + padding) : pathendx, island ? pathendy : pathstraty - (getMeasuredHeight() - 2 * padding) * pre);
        }else{
            outpath.moveTo(pathendx, pathendy);
            outpath.lineTo(island ? getMeasuredWidth()-((getMeasuredWidth() - 2 * padding) * pre + padding) : pathendx
                    , island ? pathendy : (getMeasuredHeight() - 2 * padding) * pre+padding);
        }
        outpaint.setStrokeWidth(internalStrokeWidth);
        canvas.drawPath(outpath,outpaint);


    }


}
