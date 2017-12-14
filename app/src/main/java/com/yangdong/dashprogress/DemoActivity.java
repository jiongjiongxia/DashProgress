package com.yangdong.dashprogress;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DemoActivity extends AppCompatActivity {

    @BindView(R.id.seekbar)SeekBar seekBar;
    @BindView(R.id.progress_bar_land1)
    DashProgressBar dashProgressBarland1;//横向，从左往右绘制的进度条
    @BindView(R.id.progress_bar_land2)
    DashProgressBar dashProgressBarland2;//横向，从右往左绘制的进度条、

    @BindView(R.id.progress_bar_pro1)
    DashProgressBar dashProgressBar1;//竖向。从下网上绘制的进度条
    @BindView(R.id.progress_bar_pro2)
    DashProgressBar dashProgressBar2;//竖向。从上往下绘制的进度条

    @BindView(R.id.progresscir)DashProgressCircle progressCircle;

    @BindView(R.id.chat_swipe_layout)SwitchCompat aSwitch;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dashProgressBarland1.setSmoothProgress(55);
                dashProgressBarland2.setSmoothProgress(55);
            }
        },200);


        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    dashProgressBarland1.setDash(true);
                    dashProgressBarland2.setDash(true);

                    dashProgressBar1.setDash(true);
                    dashProgressBar2.setDash(true);
                    progressCircle.setDash(true);
                }
                else {
                    dashProgressBarland1.setDash(false);
                    dashProgressBarland2.setDash(false);

                    dashProgressBar1.setDash(false);
                    dashProgressBar2.setDash(false);
                    progressCircle.setDash(false);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dashProgressBarland1.setSmoothProgress(progress);
                dashProgressBarland2.setSmoothProgress(progress);
                dashProgressBar1.setProgress(progress);
                dashProgressBar2.setProgress(progress);
                progressCircle.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
