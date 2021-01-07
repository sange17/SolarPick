package com.App.SolarPing;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class Loading extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent previous_activity = getIntent();
        String activity_checker = previous_activity.getStringExtra("activity");
        TextView t1 = findViewById(R.id.ring);
        Animation translate = AnimationUtils.loadAnimation(this, R.anim.rotate);

        //팁 띄우기
        TextView tiptext = findViewById(R.id.TipText);
        final String[] tips = {"등급별로 1단계에서 8단계까지 나누어져 있어요","순위 페이지에서는 각 도*광역시의 발전소설치지역의 순위를 나누어줘요",
                "그래프페이지의 그래프들은 선택한 지역의 정보에요","등급별 버튼을 누르면 등급에 해당하는 지역을 마커로 찍어줘요"};
        int idx = (int)(Math.random()*(tips.length-1));
        tiptext.setText("Tip : "+tips[idx]);

        t1.startAnimation(translate);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    finish();
                }
            }
        };
        loadingThread t = new loadingThread(handler,activity_checker);
        t.start();

    }
    class loadingThread extends Thread {
        private Handler handler;
        public loadingThread(Handler handler,String activity) {
            this.handler = handler;this.activity = activity;
        }
        private String activity;
        @Override
        public void run() {
            super.run();
            Message ms = new Message();
            try {
                //Thread.sleep(3500);
                if(activity.equals("main")) {
                    Thread.sleep(4000);
                }else if(activity.equals("graph")){
                    Thread.sleep(1500);
                }
                ms.what = 1;
                handler.sendEmptyMessage(ms.what);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }



}