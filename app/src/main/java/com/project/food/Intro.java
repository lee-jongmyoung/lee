package com.project.food;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class Intro extends Activity {
    private Handler handler;

    
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(Intro.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        init();
        handler.postDelayed(runnable, 1000);

        ImageView image1 = (ImageView)findViewById(R.id.imageView4);
        image1.setImageResource(R.drawable.foodtruck);
//        ImageView image2 = (ImageView)findViewById(R.id.imageView5);
//        image2.setImageResource(R.drawable.text1);
    }
    public void init() {
        handler = new Handler();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }
}
