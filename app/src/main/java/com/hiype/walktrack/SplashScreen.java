package com.hiype.walktrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class SplashScreen extends AppCompatActivity {

    private static final String VIDEO_SAMPLE = "WalkTrackLogoAnim";
    private DBHelper db;
    private VideoView videoView;
    private ProgressBar splash_progress;

    private Uri getMedia(String mediaName) {
        if(((GlobalVar) getApplication()).getNightMode()) {
            videoView.setBackground(ContextCompat.getDrawable(getBaseContext(), R.color.NightStatusBar));
            return Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.walktracklogoanim_night);
        } else {
            videoView.setBackground(ContextCompat.getDrawable(getBaseContext(), R.color.Main_SkyBlue));
            return Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.walktracklogoanim_day);
        }
    }

    private void initializePlayer() {
        Uri videoUri = getMedia(VIDEO_SAMPLE);
        videoView.setVideoURI(videoUri);
        videoView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        videoView.stopPlayback();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBHelper(this);

        NightMode.updateNightMode(getApplication(), getBaseContext());

        if(((GlobalVar) this.getApplication()).getNightMode()) {
            Log.e("NIGHTMODE" , "Enabling night mode");
            setTheme(R.style.Theme_WalkTrack_Night);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (!((GlobalVar) this.getApplication()).getNightMode()) {
            Log.e("NIGHTMODE" , "Disabling night mode");
            setTheme(R.style.Theme_WalkTrack);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            Log.e("NIGHTMODE" , "No night mode variable found, setting to follow system");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        Log.e("STARTACTIVITY", "Initialized night mode");

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        setContentView(R.layout.activity_splash_screen);

        videoView = findViewById(R.id.videoView);

        initializePlayer();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            // video started; hide the placeholder.
                            videoView.setBackground(null);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                splash_progress = findViewById(R.id.splash_progress);

                splash_progress.setVisibility(View.VISIBLE);

                Thread welcomeThread = new Thread() {

                    @Override
                    public void run() {
                        try {
                            super.run();
                            sleep(1000);  //Delay of 1 second
                        } catch (Exception e) {

                        } finally {
                            Intent i = new Intent(SplashScreen.this,
                                    MainActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                            finish();
                        }
                    }
                };
                welcomeThread.start();
            }
        });


    }
}