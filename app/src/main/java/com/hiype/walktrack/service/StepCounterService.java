package com.hiype.walktrack.service;

/** Service - for Counting the steps in the Background using Step Counter Sensor, and broadcasting Sensor values to Main Activity. */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.hiype.walktrack.Base;
import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.R;
import com.hiype.walktrack.fragments.UserFragment;

import java.util.Calendar;
import java.util.Date;

// _________ Extend Service class & implement Service lifecycle callback methods. _________ //
public class StepCounterService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCounterSensor;
    Sensor stepDetectorSensor;

    int currentStepCount;
    int currentStepsDetected;

    int stepCounter;
    int newStepCounter;

    int points;

    private DBHelper db;

    boolean serviceStopped; // Boolean variable to control the repeating timer.

    NotificationManager notificationManager;

    // --------------------------------------------------------------------------- \\
    // _ (1) declare broadcasting element variables _ \\
    // Declare an instance of the Intent class.
    Intent intent;
    // A string that identifies what kind of action is taking place.
    private static final String TAG = "StepService";
    public static final String BROADCAST_ACTION = "com.hiype.walktrack.service";
    // Create a handler - that will be used to broadcast our data, after a specified amount of time.
    private final Handler handler = new Handler();
    // Declare and initialise counter - for keeping a record of how many times the service carried out updates.
    int counter = 0;
    // ___________________________________________________________________________ \\

    Calendar calendar = Calendar.getInstance();
    String todayDate = String.valueOf(calendar.get(Calendar.MONTH))+"/" +
            String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(calendar.get(Calendar.YEAR));

    private static IntentFilter s_intentFilter;

    static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
    }

    private final BroadcastReceiver m_dateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_DATE_CHANGED)) {
                db.createStepsEntry(0);
            }
        }
    };


    /** Called when the service is being created. */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        db = new DBHelper(getBaseContext());
        registerReceiver(m_dateChangedReceiver, s_intentFilter);

        // --------------------------------------------------------------------------- \\
        // ___ (2) create/instantiate intent. ___ \\
        // Instantiate the intent declared globally, and pass "BROADCAST_ACTION" to the constructor of the intent.
        intent = new Intent(BROADCAST_ACTION);
        // ___________________________________________________________________________ \\
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service", "Start");

//        showNotification();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepCounterSensor, 1000000);
//        sensorManager.registerListener(this, stepDetectorSensor, 0);
        currentStepCount = 0;
//        currentStepsDetected = 0;
        stepCounter = 0;
        newStepCounter = 0;
        points = db.getPoints();

        serviceStopped = false;

        // --------------------------------------------------------------------------- \\
        // ___ (3) start handler ___ \\
        /////if (serviceStopped == false) {
        // remove any existing callbacks to the handler
        handler.removeCallbacks(updateBroadcastData);
        // call our handler with or without delay.
        handler.post(updateBroadcastData); // 0 seconds
        /////}
        // ___________________________________________________________________________ \\

        return START_STICKY;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service", "Stop");

        db = new DBHelper(getBaseContext());
        db.updateStepCount(newStepCounter);
        db.updatePoints(points);

        serviceStopped = true;

        unregisterReceiver(m_dateChangedReceiver);

//        dismissNotification();
    }

    /** Called when the overall system is running low on memory, and actively running processes should trim their memory usage. */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /////////////////__________________ Sensor Event. __________________//////////////////
    @Override
    public void onSensorChanged(SensorEvent event) {
        // STEP_COUNTER Sensor.
        // *** Step Counting does not restart until the device is restarted - therefore, an algorithm for restarting the counting must be implemented.
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.e(TAG, "onSensorChanged | step counter");
            int systemCountSteps = (int) event.values[0];
            Log.e(TAG, "Count steps: " + systemCountSteps);

            points = points + 2;

            db.createStepsEntry(systemCountSteps);
            db.updatePoints(points);

            // -The long way of starting a new step counting sequence.-
            /**
             int tempStepCount = countSteps;
             int initialStepCount = countSteps - tempStepCount; // Nullify step count - so that the step cpuinting can restart.
             currentStepCount += initialStepCount; // This variable will be initialised with (0), and will be incremented by itself for every Sensor step counted.
             stepCountTxV.setText(String.valueOf(currentStepCount));
             currentStepCount++; // Increment variable by 1 - so that the variable can increase for every Step_Counter event.
             */

            // -The efficient way of starting a new step counting sequence.-
            Log.e(TAG, "Step counter == 0 ? StepCounter: " + stepCounter);
            if (stepCounter == 0) { // If the stepCounter is in its initial value, then...
                stepCounter = (int) event.values[0]; // Assign the StepCounter Sensor event value to it.
                Log.e(TAG, "Stepcounter = (int) event.values[0] | StepCounter: " + stepCounter);
            }

//            stepCounter = 0;

            Log.e(TAG, " BEFORE Newstepcounter = counterSteps - stepCounter | newStepCounter: " + newStepCounter + ", countSteps: " + systemCountSteps + ", stepCounter: " + stepCounter);
            newStepCounter = systemCountSteps - stepCounter; // By subtracting the stepCounter variable from the Sensor event value - We start a new counting sequence from 0. Where the Sensor event value will increase, and stepCounter value will be only initialised once.
            Log.e(TAG, " AFTER Newstepcounter = counterSteps - stepCounter | newStepCounter: " + newStepCounter + ", countSteps: " + systemCountSteps + ", stepCounter: " + stepCounter);
        }

        // STEP_DETECTOR Sensor.
        // *** Step Detector: When a step event is detect - "event.values[0]" becomes 1. And stays at 1!
//        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
//            Log.e(TAG, "onSensorChanged | step detector");
//            int detectSteps = (int) event.values[0];
//            currentStepsDetected += detectSteps; //steps = steps + detectSteps; // This variable will be initialised with the STEP_DETECTOR event value (1), and will be incremented by itself (+1) for as long as steps are detected.
//        }

        Log.v("Service Counter", String.valueOf(newStepCounter));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    // ___________________________________________________________________________ \\


    // --------------------------------------------------------------------------- \\
    // _ Manage notification. _
//    private void showNotification() {
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//        notificationBuilder.setContentTitle("Pedometer");
//        notificationBuilder.setContentText("Pedometer session is running in the background.");
//        notificationBuilder.setSmallIcon(R.drawable.nav_home);
//        notificationBuilder.setColor(Color.parseColor("#6600cc"));
//        int colorLED = Color.argb(255, 0, 255, 0);
//        notificationBuilder.setLights(colorLED, 500, 500);
//        // To  make sure that the Notification LED is triggered.
//        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
//        notificationBuilder.setOngoing(true);
//
//        //Intent resultIntent = new Intent(this, MainActivity.class);
//        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,new Intent(),0);
//        notificationBuilder.setContentIntent(resultPendingIntent);
//
//        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//
//        notificationManager.notify(0, notificationBuilder.build());
//
//        Context mContext = getBaseContext();
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
//        Intent ii = new Intent(mContext.getApplicationContext(), Base.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);
//
//        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
//        bigText.bigText("Steps walked: " + db.getStepsWalked());
//        bigText.setBigContentTitle("StepCounter active");
//        bigText.setSummaryText("Tracking");
//
//        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setSmallIcon(R.drawable.notif_icon);
//        mBuilder.setContentTitle("StepCounterContentTitle");
//        mBuilder.setContentText("ContentText");
//        mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
//        mBuilder.setStyle(bigText);
//        mBuilder.setOngoing(true);
//
//        notificationManager =
//                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//
//// === Removed some obsoletes
//        String channelId = "StepCounterNotification";
//        NotificationChannel channel = new NotificationChannel(
//                channelId,
//                "Step Counter Notification",
//                NotificationManager.IMPORTANCE_HIGH);
//        notificationManager.createNotificationChannel(channel);
//        mBuilder.setChannelId(channelId);
//
//        notificationManager.notify(0, mBuilder.build());
//    }

//    private void dismissNotification() {
//        notificationManager.cancel(0);
//    }
    // ______________________________________________________________________________________ \\


    // --------------------------------------------------------------------------- \\
    // ___ (4) repeating timer ___ \\
    private Runnable updateBroadcastData = new Runnable() {
        public void run() {
            if (!serviceStopped) { // Only allow the repeating timer while service is running (once service is stopped the flag state will change and the code inside the conditional statement here will not execute).
                // Call the method that broadcasts the data to the Activity..
                broadcastSensorValue();
                // Call "handler.postDelayed" again, after a specified delay.
                handler.postDelayed(this, 1000);
            }
        }
    };
    // ___________________________________________________________________________ \\

    // --------------------------------------------------------------------------- \\
    // ___ (5) add  data to intent ___ \\
    private void broadcastSensorValue() {
        Log.e(TAG, "Data to Activity");
        // add step counter to intent.
        intent.putExtra("Points_Int", points);
        intent.putExtra("Points", String.valueOf(points));
        intent.putExtra("Counted_Step_Int", newStepCounter);
        intent.putExtra("Counted_Step", String.valueOf(newStepCounter));
//        intent.putExtra("Detected_Step_Int", currentStepsDetected);
//        intent.putExtra("Detected_Step", String.valueOf(currentStepsDetected));
        // call sendBroadcast with that intent  - which sends a message to whoever is registered to receive it.
        sendBroadcast(intent);
    }
    // ___________________________________________________________________________ \\

}
