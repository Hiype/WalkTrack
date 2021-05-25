package com.hiype.walktrack;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

public class Timer extends AppCompatActivity {
    TextView timeCounter;
    Boolean counting;

    public Timer (TextView timeCounter) {
        this.timeCounter = timeCounter;
        counting = true;
    }

    CountDownTimer cdt = new CountDownTimer(300000, 1000) {

        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished > 60000) {
                timeCounter.setText(String.format("%01d min %02d sec", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes((TimeUnit.MILLISECONDS.toHours(millisUntilFinished))), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                counting = true;
            } else {
                timeCounter.setText(String.format("%02d sec", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                counting = true;
            }
        }

        public void onFinish() {
            timeCounter.setText("Invalid code!");
            counting = false;
        }
    }.start();

    public boolean isCounting () {
        return counting;
    }

}
