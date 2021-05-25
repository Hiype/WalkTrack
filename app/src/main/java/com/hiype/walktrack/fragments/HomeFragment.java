 package com.hiype.walktrack.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.DistanceCounter;
import com.hiype.walktrack.Lottie;
import com.hiype.walktrack.MySqlApi;
import com.hiype.walktrack.adapters.FriendsTopListAdapter;
import com.hiype.walktrack.GlobalVar;
import com.hiype.walktrack.R;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;
import com.hiype.walktrack.service.StepCounterService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView topFriends;
    private FriendsTopListAdapter topFriendsAdapter;
    private ImageView settingsNavigationButton, friendsNavigationButton;
    private DrawerLayout drawerLayout;
    private ArrayList<JSONObject> friend_users;
    private DBHelper db;
    private TextView steps_walked_today, distance_walked_today;
    private Switch night_switch;
    private int user_height;
    private TextView points_counter;
    private NavigationView navigationView;
    private MenuItem english_item;
    private MenuItem latvian_item;

    TextView stepCountTxV;
    TextView stepDetectTxV;

    Button startServiceBtn;
    Button stopServiceBtn;

    private int CountedStep;
    String DetectedStep;
    static final String State_Count = "Counter";
    static final String State_Detect = "Detector";

    private boolean isServiceStopped;

    private LottieAnimationView lottieAnimationView;

    RelativeLayout parentLayout;
    int pLayoutHeight;
    int pLayoutWidth;
    RelativeLayout relativeLayout;
    int rLayoutT;
    int rLayoutB;
    int rLayoutL;
    int rLayoutR;
    int rLayoutHeight;
    int rLayoutWidth;
    LinearLayout childLayout;

    ImageView imageView2;

    private Intent intent;
    private static final String TAG = "SensorEvent";

    private NavigationView settingsNavigationView, friendsNavigationView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //StepsFormatter stepsFormatter = new StepsFormatter();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBHelper(getContext());

        friend_users = new ArrayList<JSONObject>();

        getFriends();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        user_height = db.getUserHeight();

        if (db.getLanguage().equals("Latvian")) {
            Log.e("MAINACTIVITY LANG", "Global var lang was Latvian");
            Locale locale = new Locale("lv");
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, displayMetrics);
            ((GlobalVar) getActivity().getApplication()).setLanguage("Latvian");
        } else {
            Log.e("MAINACTIVITY LANG", "Global var lang was English");
            Locale locale = new Locale("en");
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, displayMetrics);
            ((GlobalVar) getActivity().getApplication()).setLanguage("English");
        }

        Log.e("HOME FRAGMENT LANG", "Start-up language: " + ((GlobalVar) getActivity().getApplication()).getLanguage());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DBHelper db = new DBHelper(getContext());

        SensorManager sensorManager = (SensorManager) requireActivity().getSystemService(SENSOR_SERVICE);

        if (isMyServiceRunning()) {
            init();
        } else {
            Log.e("HOMEFRAGMENT", "Service is already running!");
            isServiceStopped = false;
        }


        View view = getView();

        if(getView() != null) {
            topFriends = (RecyclerView) view.findViewById(R.id.topFriendsRecyclerView);

            settingsNavigationButton = (ImageView) view.findViewById(R.id.settingsNavigationButton);

            drawerLayout = (DrawerLayout) view.findViewById(R.id.settingsNavigation);
            navigationView = (NavigationView) view.findViewById(R.id.settings_navigation_view);

            steps_walked_today = (TextView) view.findViewById(R.id.steps_walked_today);
            distance_walked_today = (TextView) view.findViewById(R.id.distance_walked_today);
            points_counter = (TextView) view.findViewById(R.id.points_counter);

        } else {
            Log.e("HOMEFRAGMENT", "Get view was null!");
        }

        lottieAnimationView = (LottieAnimationView) getActivity().findViewById(R.id.animationView);
        navigationView.setItemIconTintList(null);

        //Sets the switch in drawer layout
        night_switch = (Switch) navigationView.getMenu().findItem(R.id.night_switch_drawer).getActionView();
        night_switch.setChecked(((GlobalVar) getActivity().getApplication()).getNightMode());
        night_switch.setOnCheckedChangeListener(this);

        //Side menu language select english
        english_item = (MenuItem) navigationView.getMenu().findItem(R.id.english_language_selection);
        english_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(!db.getLanguage().equals("English")) {
                    Log.e("MAINACTIVITY LANG", "Global var lang was English");
                    Locale locale = new Locale("en");
                    Resources resources = getResources();
                    DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                    Configuration configuration = resources.getConfiguration();
                    configuration.setLocale(locale);
                    resources.updateConfiguration(configuration, displayMetrics);
                    ((GlobalVar) getActivity().getApplication()).setLanguage("English");
                    db.setLanguage("English");
                    getActivity().recreate();

                    return true;
                } else {
                    Toast.makeText(getContext(), "English is already set", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });

        //Side menu language select latvian
        latvian_item = (MenuItem) navigationView.getMenu().findItem(R.id.latvian_language_selection);
        latvian_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(!db.getLanguage().equals("Latvian")) {
                    Log.e("MAINACTIVITY LANG", "Global var lang was Latvian");
                    Locale locale = new Locale("lv");
                    Resources resources = getResources();
                    DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                    Configuration configuration = resources.getConfiguration();
                    configuration.setLocale(locale);
                    resources.updateConfiguration(configuration, displayMetrics);
                    ((GlobalVar) getActivity().getApplication()).setLanguage("Latvian");
                    db.setLanguage("Latvian");
                    getActivity().recreate();

                    return true;
                } else {
                    Toast.makeText(getContext(), "Latviešu valoda jau ir uzlikta", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });

        steps_walked_today.setText(String.valueOf(db.getStepsWalked()));
        Log.e("HOMEFRAGMENT", "In db steps walked: " + db.getStepsWalked());

//        getFriends.execute();
//
//        while(!((GlobalVar) getActivity().getApplication()).getfriendTopListExecuted() || friend_users == null)
//        {
//            Log.e("Waiting", "WAITING");
//        }
//
//        topFriendsAdapter = new FriendsTopListAdapter(friend_users);

//        ArrayList<List<String>> friends = new ArrayList<List<String>>();
//        ArrayList<String> row1 = new ArrayList<>(2);
//
//        row1.add(0, "Jānis");
//        row1.add(1, "18293" + " Steps");
//        friends.add(row1);
//        Log.e("ARRAY", "Friends array: " + friends);
//
//        ArrayList<String> row2 = new ArrayList<>(2);
//        row2.add(0, "Kristiāns (You)");
//        row2.add(1, "15234" + " Steps");
//        friends.add(row2);
//        Log.e("ARRAY", "Friends array: " + friends);
//
//        ArrayList<String> row3 = new ArrayList<>(2);
//        row3.add(0, "Liene");
//        row3.add(1, "9832" + " Steps");
//        friends.add(row3);
//        Log.e("ARRAY", "Friends array: " + friends);
//
//        ArrayList<String> row4 = new ArrayList<>(2);
//        row4.add(0, "Raitis");
//        row4.add(1, "0" + " Steps");
//        friends.add(row4);
//        Log.e("ARRAY", "Friends array: " + friends);

//        navigationView.getMenu().findItem(R.id.friends_list_item).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if(item == ) {
//
//                }
//                return false;
//            }
//        });

        settingsNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("DRAWER", "Menu button clicked");
                if(drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);

                }
                drawerLayout.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

//                NavigationMenuItemView switch_toggle;
//                switch_toggle = (NavigationMenuItemView) view.findViewById(R.id.night_switch_drawer);
//
//                switch_toggle.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.e("DRAWER NIGHTSWITCH", "OnClickListener started initializing");
//                        boolean isChecked = switch_toggle.isEnabled();
//                        Activity activity = getActivity();
//
//                        if (isChecked) {
//                            Log.e("SWITCH", String.valueOf(isChecked));
//                            ((GlobalVar) activity.getApplication()).setNightMode(true);
//                            lottieAnimationView.setAnimation(Lottie.LOTTIE_NIGHT);
//                            db.updateNightMode(true, db.getCurrentUserID());
//
//                        } else {
//                            Log.e("SWITCH", String.valueOf(isChecked));
//                            ((GlobalVar) activity.getApplication()).setNightMode(false);
//                            lottieAnimationView.setAnimation(Lottie.LOTTIE_DAY);
//                            db.updateNightMode(false, db.getCurrentUserID());
//                        }
//                  startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                  finish();
//                        activity.recreate();
//                    }
//                });

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if(!drawerLayout.isDrawerOpen(Gravity.LEFT) && !drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("HOMEFRAGMENT", "onStart executed");
        requireActivity().registerReceiver(broadcastReceiver, new IntentFilter(StepCounterService.BROADCAST_ACTION));
//        steps_walked_today = requireView().findViewById(R.id.steps_walked_today);
//        distance_walked_today = requireView().findViewById(R.id.distance_walked_today);

//        steps_walked_today.setText(db.getStepCount());

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("HOMEFRAGMENT", "onResume executed");
//        distance_walked_today = requireView().findViewById(R.id.distance_walked_today);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(!isServiceStopped) {
            try {
                requireActivity().unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {e.printStackTrace();}

        }
    }

    // Initialise views.
    public void init() {

        isServiceStopped = true; // variable for managing service state - required to invoke "stopService" only once to avoid Exception.

        // register our BroadcastReceiver by passing in an IntentFilter. * identifying the message that is broadcasted by using static string "BROADCAST_ACTION".
        if(requireActivity().registerReceiver(broadcastReceiver, new IntentFilter(StepCounterService.BROADCAST_ACTION)) != null) {
            Log.e(TAG, "Receiver registered");
        } else {
            Log.e(TAG, "Intent filter didnt match");
        }
        isServiceStopped = false;


//        ------------Service stopper--------
//
//        if (!isServiceStopped) {
//            // call unregisterReceiver - to stop listening for broadcasts.
//            activity.unregisterReceiver(broadcastReceiver);
//            // stop Service.
//            activity.stopService(new Intent(getContext(), StepCounterService.class));
//            isServiceStopped = true;
//        }
        // ___________________________________________________________________________ \\

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // call updateUI passing in our intent which is holding the data to display.
            updateViews(intent);
        }
    };

    private void updateViews(Intent intent) {

        // retrieve data out of the intent.
        CountedStep = intent.getIntExtra("Counted_Step_Int", 0);
        int points = intent.getIntExtra("Points_Int", 0);
        Log.d(TAG, String.valueOf(CountedStep));

        points_counter.setText("Points " + points);
        steps_walked_today.setText(String.valueOf(CountedStep));
        distance_walked_today.setText(DistanceCounter.calculateDistance(user_height, CountedStep) + " km");

    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (StepCounterService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked) {
            Log.e("SWITCH", String.valueOf(isChecked));
            ((GlobalVar) getActivity().getApplication()).setNightMode(true);
            lottieAnimationView.setAnimation(Lottie.LOTTIE_NIGHT);
            Log.e("LOTTIE HOME FRAGMENT", "Lottie set to NIGHT");
            db.updateNightMode(true);
        } else {
            Log.e("SWITCH", String.valueOf(isChecked));
            ((GlobalVar) getActivity().getApplication()).setNightMode(false);
            lottieAnimationView.setAnimation(Lottie.LOTTIE_DAY);
            Log.e("LOTTIE HOME FRAGMENT", "Lottie set to DAY");
            db.updateNightMode(false);
        }

        Log.e("HOMEFRAGMENT RECREATE", "Recreating base activity!");
        getActivity().recreate();
    }

    private void getFriends() {
        MySqlApi.getUserFriends(getContext());
    }
}