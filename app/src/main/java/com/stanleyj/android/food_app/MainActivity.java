package com.stanleyj.android.food_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button bR, bL;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //        Shared preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean check_log = sharedPref.getBoolean("Login", false);
        if (check_log) {
            Intent log = new Intent(this, LoginActivity.class);
            startActivity(log);
            finish();
        }
        Animation bounce = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.bounce);
        bR = findViewById(R.id.reg);
        bL = findViewById(R.id.log);
        imageView = findViewById(R.id.img);
        fadeIn2(imageView);
        fadeIn3(bL);
        fadeIn3(bR);
        bL.startAnimation(bounce);
        bL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        bR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }

    private void fadeIn2(View view) {
//        creating a fade-in animation
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
//        how long will it take in milliseconds ie 1000msec = 1sec
        animation.setDuration(3000);
//        start animation
        view.startAnimation(animation);
//        make view visible after animation
        view.setVisibility(View.VISIBLE);
    }

    private void fadeIn3(View view) {
//        creating a fade-in animation
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
//        how long will it take in milliseconds ie 1000msec = 1sec
        animation.setDuration(5000);
//        start animation
        view.startAnimation(animation);
//        make view visible after animation
        view.setVisibility(View.VISIBLE);
    }

}
