package com.hiype.walktrack.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.hiype.walktrack.Base;
import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.DistanceCounter;
import com.hiype.walktrack.GlobalVar;
import com.hiype.walktrack.Lottie;
import com.hiype.walktrack.R;
import com.hiype.walktrack.adapters.FriendsTopListAdapter;
import com.hiype.walktrack.adapters.PCListAdapter;
import com.hiype.walktrack.adapters.StoreListAdapter;
import com.hiype.walktrack.service.StepCounterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoreFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageView settingsNavigationButton, friendsNavigationButton;
    DrawerLayout drawerLayout;
    RecyclerView storeList;
    StoreListAdapter storeListAdapter;
    DBHelper db;
    Button adButton;
    TextView big_points_counter;
    private Switch night_switch;
    private LottieAnimationView lottieAnimationView;
    private NavigationView navigationView;
    private MenuItem english_item;
    private MenuItem latvian_item;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoreFragment newInstance(String param1, String param2) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (((GlobalVar) getActivity().getApplication()).getLanguage().equals("Latvian")) {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        requireActivity().registerReceiver(broadcastReceiver, new IntentFilter(StepCounterService.BROADCAST_ACTION));

        if(getView() != null) {
            settingsNavigationButton = (ImageView) view.findViewById(R.id.settingsNavigationButton);

            drawerLayout = (DrawerLayout) view.findViewById(R.id.settingsNavigation);
            navigationView = (NavigationView) view.findViewById(R.id.settings_navigation_view);

            storeList = (RecyclerView) view.findViewById(R.id.store_recycler_view);

            adButton = (Button) view.findViewById(R.id.ad_button);

            big_points_counter = (TextView) view.findViewById(R.id.big_points_counter);
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

                return false;
            }
        });

        //Side menu language select latvian
        latvian_item = (MenuItem) navigationView.getMenu().findItem(R.id.latvian_language_selection);
        latvian_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
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

                return false;
            }
        });

        ArrayList<List<String>> store = new ArrayList<List<String>>();
        ArrayList<String> row1 = new ArrayList<>(2);

        row1.add(0, "LULW");
        row1.add(1, "10000");
        row1.add(2, "7");
        store.add(row1);
        Log.e("ARRAY", "Friends array: " + store);

        ArrayList<String> row2 = new ArrayList<>(2);
        row2.add(0, "OMEGALUL");
        row2.add(1, "15000");
        row2.add(2, "8");
        store.add(row2);
        Log.e("ARRAY", "Friends array: " + store);

        ArrayList<String> row3 = new ArrayList<>(2);
        row3.add(0, "Pepe");
        row3.add(1, "20000");
        row3.add(2, "9");
        store.add(row3);
        Log.e("ARRAY", "Friends array: " + store);

        ArrayList<String> row4 = new ArrayList<>(2);
        row4.add(0, "PepeLaugh");
        row4.add(1, "30000");
        row4.add(2, "10");
        store.add(row4);

        ArrayList<String> row5 = new ArrayList<>(2);
        row5.add(0, "PogChamp");
        row5.add(1, "50000");
        row5.add(2, "11");
        store.add(row5);

        ArrayList<String> row6 = new ArrayList<>(2);
        row6.add(0, "Sadge");
        row6.add(1, "75000");
        row6.add(2, "12");
        store.add(row6);

        ArrayList<String> row7 = new ArrayList<>(2);
        row7.add(0, "PepePls");
        row7.add(1, "1000000");
        row7.add(2, "13");
        store.add(row7);

        ArrayList<String> row8 = new ArrayList<>(2);
        row8.add(0, "PeepoArrive");
        row8.add(1, "2000000");
        row8.add(2, "14");
        store.add(row8);

        ArrayList<String> row9 = new ArrayList<>(2);
        row9.add(0, "CatJam");
        row9.add(1, "5000000");
        row9.add(2, "15");
        store.add(row9);

        Log.e("ARRAY", "Friends array: " + store);

        storeList.setLayoutManager(new LinearLayoutManager(getContext()));
        storeListAdapter = new StoreListAdapter(store);
        storeList.setAdapter(storeListAdapter);

        adButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Base base = (Base) getActivity();
                RewardedAd mRewardedAd = base.getmRewardedAd();

                Log.e("AD BUTTON", "Clicked");

                if (mRewardedAd != null) {
                    Activity activityContext = getActivity();
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d("TAG", "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "The ad is not ready yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        settingsNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("DRAWER", "Menu button clicekd");
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // call updateUI passing in our intent which is holding the data to display.
            updateViews(intent);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        requireActivity().unregisterReceiver(broadcastReceiver);
    }

    private void updateViews(Intent intent) {
        // retrieve data out of the intent.
        String points = intent.getStringExtra("Points");

        big_points_counter.setText(points);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DBHelper db = new DBHelper(getContext());

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
}