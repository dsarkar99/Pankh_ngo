package com.debu.asus.pankh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class splash extends AppCompatActivity   {

    private static int SPLASH_TIMEOUT = 3000;
    public static final String MyPREFERENCES = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        transparentToolbar();

        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1800);
        rotate.setInterpolator(new LinearInterpolator());



        ImageView imageView = (ImageView) findViewById(R.id.imageView3);

        Glide.with(splash.this).load(R.drawable.logo).into(imageView);

        //imageView.startAnimation(rotate);

        imageView.setAnimation(AnimationUtils.loadAnimation(splash.this, R.anim.fade_in));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences(LoginActivity.MyPREFERENCES, 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
                boolean hasLoggedIn = settings.getBoolean("hasloggedin", false);

                if (hasLoggedIn) {
                    //Go directly to main activity.
                    try {

                        Intent i = new Intent(splash.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(splash.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    // Stop current activity
                    finish();
                } else {
                    Intent i = new Intent(splash.this, LoginActivity.class);
                    startActivity(i);
                }
            }

        }, SPLASH_TIMEOUT);
    }

    private void transparentToolbar() {
        if (Build.VERSION.SDK_INT >= 17 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


}
