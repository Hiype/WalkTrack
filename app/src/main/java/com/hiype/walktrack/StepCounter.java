//package com.hiype.walktrack;
//
//import android.app.Activity;
//import android.content.Context;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.TextView;
//
//import org.w3c.dom.Text;
//
//public class StepCounter extends Activity implements SensorEventListener {
//
//    private SensorManager sensorManager;
//    private Sensor sensor;
//    private boolean isSensorPresent = false;
//    private TextView stepsToday, distanceToday;
//    private DistanceCounter distanceCounter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        stepsToday = (TextView) findViewById(R.id.steps_walked_today);
//        distanceToday = (TextView) findViewById(R.id.distance_walked_today);
//        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
//
//        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
//            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//            isSensorPresent = true;
//        } else {
//            isSensorPresent = false;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        if(isSensorPresent) {
//            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
//        }
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if(stepsToday != null) {
//            stepsToday.setText(String.valueOf(event.values[0]));
//            //distanceToday.setText(distanceCounter.calculateDistance(Double.parseDouble()));
//            Log.e("STEPCOUNTER", "Updating step count");
//        } else {
//            Log.d("STEPCOUNTER", "Step textView does not exist yet");
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
//}
