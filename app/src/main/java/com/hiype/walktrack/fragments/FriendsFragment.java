package com.hiype.walktrack.fragments;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.GlobalVar;
import com.hiype.walktrack.Lottie;
import com.hiype.walktrack.adapters.PCListAdapter;
import com.hiype.walktrack.R;
import com.hiype.walktrack.adapters.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class FriendsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageView settingsNavigationButton, friendsNavigationButton;
    DrawerLayout drawerLayout;
    RecyclerView pcList;
    PCListAdapter pcListAdapter;
    DBHelper db;
    Button download;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private Switch night_switch;
    private LottieAnimationView lottieAnimationView;
    private NavigationView navigationView;
    private MenuItem english_item;
    private MenuItem latvian_item;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(String param1, String param2) {
        FriendsFragment fragment = new FriendsFragment();
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
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.list));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.add));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setBackgroundColor(Color.TRANSPARENT);

        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager = (ViewPager2) view.findViewById(R.id.friends_pager);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.e("VIEWPAGER", "onPageScrolled, position: " + position + ", positionOffset: " + positionOffset);
                if(positionOffset == 0f) {
                    tabLayout.selectTab(tabLayout.getTabAt(position));
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = new DBHelper(getContext());
        View view = getView();

        if(getView() != null) {
            settingsNavigationButton = (ImageView) view.findViewById(R.id.settingsNavigationButton2);

            drawerLayout = (DrawerLayout) view.findViewById(R.id.settingsNavigation);

            navigationView = (NavigationView) view.findViewById(R.id.settings_navigation_view);
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