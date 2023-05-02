package com.cjz.wasif.lifesaverrider;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreenCustomer extends AppCompatActivity {
    TextView sub;
    Animation frombottom;
    private static int SPLASH_TIME_OUT=3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_customer);
        sub=(TextView) findViewById(R.id.sub);

        frombottom= AnimationUtils.loadAnimation(this,R.anim.frombottom);

        sub.setAnimation(frombottom);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent splash =new Intent(SplashScreenCustomer.this,MainActivity.class);
                startActivity(splash);
                finish();
            }
        },SPLASH_TIME_OUT);

    }
}

