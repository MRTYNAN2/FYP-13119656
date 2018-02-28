package com.fyp.rory.fyp.Utilitys;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Rory on 20/02/2018.
 * This class just simplfies storing prefences which I use to track if a user is using the app for the 1st time
 */

public class FBPreferences {

    private static FBPreferences ourInstance;

    public static FBPreferences getInstance() {
        if (ourInstance == null){
            ourInstance = new FBPreferences();
        }
        return ourInstance;
    }

    public void setPref(Context context){
            SharedPreferences.Editor editor = context.getSharedPreferences("FBUser", MODE_PRIVATE).edit();
                editor.putBoolean("init", true);
                //editor.apply();
                editor.commit();
    }

    public boolean getPref(Context context){
        SharedPreferences prefs = context.getSharedPreferences("FBUser", MODE_PRIVATE);
            boolean firstTime = prefs.getBoolean("init",false);
            if (firstTime)
                return true;
            else
                return false;
    }
}
