package com.fyp.rory.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.fyp.rory.fyp.Activitys.Login_Activity;

public class Splash_Screen extends AppCompatActivity {

    private Handler handler;
    private Runnable runny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        int delay = 1200;
        handler = new Handler();
        runny = new Runnable() {
            @Override
            public void run() {
//                if (isFacebookLoggedIn()) {
//                    Intent mainIntent = new Intent(Splash_Screen.this,
//                            MainActivity.class);
//                    Splash_Screen.this.startActivity(mainIntent);
//                    Splash_Screen.this.finish();
//                } else {
                    Intent mainIntent = new Intent(Splash_Screen.this, Login_Activity.class);
                    Splash_Screen.this.startActivity(mainIntent);
                    Splash_Screen.this.finish();
              //  }
            }
        };
        handler.postDelayed(runny, delay);
    }

//    public boolean isFacebookLoggedIn(){
//        return AccessToken.getCurrentAccessToken() != null;
//    }

    @Override
    public void onBackPressed() {
        if (handler != null) {
            handler.removeCallbacks(runny);
        }
        super.onBackPressed();
    }
}
