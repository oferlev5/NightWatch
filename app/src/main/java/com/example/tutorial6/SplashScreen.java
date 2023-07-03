package com.example.tutorial6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private  static int SPLASH_SCREEN=5000;

    Animation topAnim,textAnim,botAnim;
    ImageView logo, baby;
    TextView headline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("idan - in splash");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);



        //ANIMAATION
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        textAnim = AnimationUtils.loadAnimation(this,R.anim.text_animation);
        botAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //hooks
        logo = findViewById(R.id.imageView2);
        baby = findViewById(R.id.imageView3);
        headline = findViewById(R.id.headline);


        logo.setAnimation(topAnim);
        headline.setAnimation(textAnim);
        baby.setAnimation(botAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, SignupActivity.class);
                startActivity(intent);
                finish();


            }
        },SPLASH_SCREEN);
    }
}