package com.hiype.walktrack;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hiype.walktrack.fragments.FriendsFragment;
import com.hiype.walktrack.fragments.HomeFragment;
import com.hiype.walktrack.fragments.StatsFragment;
import com.hiype.walktrack.fragments.StoreFragment;
import com.hiype.walktrack.fragments.UserFragment;
import com.hiype.walktrack.http.WebServiceURL;
import com.hiype.walktrack.service.StepCounterService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class Base extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final int SAMPLING_FREQUENCY = 100;

    public BottomNavigationView bttmNavigationView;
    public TextView testText;
    public ListView sensorListView;
    private ListAdapter listAdapter;
    private DBHelper db;
    private int currentSteps;
    private Intent intent;
    boolean isServiceStopped;
    private String countedStep;
    private String DetectedStep;

    private RewardedAd mRewardedAd;
    private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(this, StepCounterService.class);

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

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        if(((GlobalVar) this.getApplication()).getLanguage().equals("Latvian")) {
            Locale locale = new Locale("lv");
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, displayMetrics);
            ((GlobalVar) getApplication()).setLanguage("Latvian");
        } else {
            Locale locale = new Locale("en");
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, displayMetrics);
            ((GlobalVar) getApplication()).setLanguage("English");
        }


        db = new DBHelper(getApplicationContext());
        int user_id = db.getCurrentUserID();

        final OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .build();
        Request request = new Request.Builder()
                .url(WebServiceURL.URL_GET_LANGUAGE)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.getStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.e("OKHTTP RESPONSE", "Code: " + String.valueOf(response.code()) + " String: " + response.body().string());
                } else {
                    Log.e("OKHTTP RESPONSE", "Code: " + response.code());
                }
            }
        });




        setContentView(R.layout.base);

        // --------------------------------------------------------------------------- \\


        bttmNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        //getDataForList();

//        Intent stepsIntent = new Intent(getApplicationContext(), StepsService.class);
//        startService(stepsIntent);

        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animationView);

        if(((GlobalVar) this.getApplication()).getNightMode()) {
            lottieAnimationView.setAnimation(Lottie.LOTTIE_NIGHT);
            Log.e("LOTTIE BASE", "Lottie set to night!");
        } else {
            lottieAnimationView.setAnimation(Lottie.LOTTIE_DAY);
            Log.e("LOTTIE BASE", "Lottie set to day!");
        }

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            if (!isMyServiceRunning()) {
                isServiceStopped = false;
                intent = new Intent(this, StepCounterService.class);
                init();
            } else {
                Log.e("HOMEFRAGMENT", "Service is already running!");
                isServiceStopped = false;
            }
        } else {
            Toast.makeText(getBaseContext(), "This device does not support stepcounting!", Toast.LENGTH_LONG).show();
            Log.e("HOMEFRAGMENT", "TYPE_STEP_COUNTER was null");
            isServiceStopped = true;
        }

// Starts the fragment 2 times
//        if (savedInstanceState == null) {
//            //Bundle bundle = new Bundle();
//            //bundle.putParcelable("ListView", (Parcelable) sensorListView);
//
//            getSupportFragmentManager().beginTransaction()
//                    .setReorderingAllowed(true)
//                    .add(R.id.fragment_container_view, HomeFragment.class, null)
//                    .commit();
//        }

        bttmNavigationView.setOnNavigationItemSelectedListener(this);
        bttmNavigationView.setSelectedItemId(R.id.homeNav);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        db = new DBHelper(this);

        switch (item.getItemId())
        {
            case R.id.homeNav:
                switchToHome();
                break;

            case R.id.storeNav:
                switchToStore();

                AdRequest adRequest = new AdRequest.Builder().build();

                RewardedAd.load(this, "ca-app-pub-6947649147970605/1918418842",
                        adRequest, new RewardedAdLoadCallback(){
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error.
                                Log.d(TAG, loadAdError.getMessage());
                                mRewardedAd = null;
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                mRewardedAd = rewardedAd;
                                Log.d(TAG, "onAdFailedToLoad");
                            }
                        }
                );
                break;

            case R.id.statsNav:
                switchToStats();
                break;

            case R.id.computerNav:
                switchToFriends();
                break;

            case R.id.userNav:
                switchToUser();
                break;
        }
        return true;
    }

    public RewardedAd getmRewardedAd() {
        return mRewardedAd;
    }

    public void switchToHome() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container_view, new HomeFragment()).commit();
    }

    public void switchToStore() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container_view, new StoreFragment()).commit();
    }

    public void switchToFriends() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container_view, new FriendsFragment()).commit();
    }

    public void switchToStats() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container_view, new StatsFragment()).commit();
    }

    public void switchToUser() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.fragment_container_view, new UserFragment()).commit();
    }

    @Override
    protected void onStop() {
        // call the superclass method first
        super.onStop();
//        MySqlApi.updateDb(db.getStepsAndPoints(), getBaseContext(), getApplication());
        this.stopService(new Intent(getApplicationContext(), StepCounterService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MySqlApi.updateDb(db.getStepsAndPoints(), getBaseContext(), getApplication());

    }

    protected void onPause() {
        super.onPause();
    }

    public void init() {

        isServiceStopped = true; // variable for managing service state - required to invoke "stopService" only once to avoid Exception.

        // start Service.
        Log.e("HOMEFRAGMENT", "Starting service");
        Log.e("HOMEFRAGMENT", String.valueOf(startService(new Intent(getApplicationContext(), StepCounterService.class))));

        if (isMyServiceRunning()) {
            Log.e("HOMEFRAGMENT", "Service is running");
        } else {
            Log.e("HOMEFRAGMENT", "Service is NOT running");
        }

        isServiceStopped = false;
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (StepCounterService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//    public void getDataForList() {
//        db = new DBHelper(this);
//        stepCountList = db.readStepsEntries();
//    }

//    private class ListAdapter extends BaseAdapter {
//
//        private TextView mDateStepCountText;
//
//        @Override
//        public int getCount() {
//
//            return stepCountList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//
//            return stepCountList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            if(convertView == null){
//
//                convertView = getLayoutInflater().inflate(R.layout.fragment_home, parent, false);
//            }
//
//            mDateStepCountText = (TextView)convertView.findViewById(R.id.steps_walked_today);
//            mDateStepCountText.setText(stepCountList.get(position).date + " - Total Steps: " + String.valueOf(stepCountList.get(position).stepCount));
//
//            return convertView;
//        }
//    }

}
