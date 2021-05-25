package com.hiype.walktrack;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.airbnb.lottie.animation.content.Content;

public class NightMode extends Activity {

    public static int updateNightMode(Application application, Context context) {

        DBHelper db = new DBHelper(context);

        if(!db.doesUserExist()) {
                int nightModeFlags =
                        application.getResources().getConfiguration().uiMode &
                                Configuration.UI_MODE_NIGHT_MASK;
                switch (nightModeFlags) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        ((GlobalVar) application).setNightMode(true);
                        Log.e("GLOBAL FUNC NIGHT MODE", "Night mode set TRUE");
                        return 1;

                    case Configuration.UI_MODE_NIGHT_NO:
                        ((GlobalVar) application).setNightMode(false);
                        Log.e("GLOBAL FUNC NIGHT MODE", "Night mode set FALSE");
                        return 2;

                    case Configuration.UI_MODE_NIGHT_UNDEFINED:
                        Log.e("NIGHTMODE", "Nightmode undefined");
                        return 3;
                }
        } else {
            Log.e("GLOBAL FUNC NIGHT MODE", "There is user in db");
            if(db.getNightMode()) {
                ((GlobalVar) application).setNightMode(true);
                Log.e("GLOBAL FUNC NIGHT MODE", "Night mode set TRUE from existing user");
            } else {
                ((GlobalVar) application).setNightMode(false);
                Log.e("GLOBAL FUNC NIGHT MODE", "Night mode set FALSE from existing user");
            }
            return 0;
        }
        return -1;
    }

}
