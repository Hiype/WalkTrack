package com.hiype.walktrack;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DateStepUpdater {

    public static boolean updateStepsDates (Context context) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        //Background work
        executor.execute(() -> {
            DBHelper db = new DBHelper(context);
            ArrayList<ArrayList<String>> allSteps = db.getAllSteps();
            Calendar calendar = Calendar.getInstance();
            calendar.before(7);


            handler.post(() -> {

            });
        });

        return true;
    }
}
