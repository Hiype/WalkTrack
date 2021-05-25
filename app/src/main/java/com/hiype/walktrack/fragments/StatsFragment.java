package com.hiype.walktrack.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.GlobalVar;
import com.hiype.walktrack.Lottie;
import com.hiype.walktrack.MySqlApi;
import com.hiype.walktrack.R;
import com.hiype.walktrack.adapters.FriendsTopListAdapter;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView settingsNavigationButton, friendsNavigationButton;
    private DrawerLayout drawerLayout;
    private BarChart chart;
    private RecyclerView topFriends;
    private FriendsTopListAdapter topFriendsAdapter;
    private Switch night_switch;
    private LottieAnimationView lottieAnimationView;
    private NavigationView navigationView;
    private MenuItem english_item;
    private MenuItem latvian_item;
    private ArrayList<JSONObject> friend_users;
    private TextView no_steps_data_text;
    private Boolean isStepDataPresent;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isStepDataPresent = true;

        DBHelper db = new DBHelper(getContext());

        friend_users = new ArrayList<JSONObject>();

        getFriends();

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

        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DBHelper db = new DBHelper(getContext());

        View view = getView();

        if(getView() != null) {
            topFriends = (RecyclerView) view.findViewById(R.id.overallTopFriendsRecyclerView);

            settingsNavigationButton = (ImageView) view.findViewById(R.id.settingsNavigationButton);
            no_steps_data_text = (TextView) view.findViewById(R.id.no_step_data_text);

            drawerLayout = (DrawerLayout) view.findViewById(R.id.settingsNavigation);
            chart = (BarChart) view.findViewById(R.id.weeksChart);

            navigationView = (NavigationView) view.findViewById(R.id.settings_navigation_view);
        } else {
            Log.e("STATS FRAGMENT", "View was null");
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

        ArrayList<ArrayList<String>> all_steps =  db.getAllSteps();
        BarEntry[] steps = new BarEntry[all_steps.size()];
        List<BarEntry> entries = new ArrayList<BarEntry>();

        if(all_steps != null && all_steps.size() > 6) {
            int days = 7;

            for (int i = 0; i < all_steps.size(); i++) {
                steps[i] = new BarEntry((float) i, Float.parseFloat(all_steps.get(i).get(1)));
                Log.e("CHART Bar entries", "Iteration: " + i);
            }

            for (int i = 0; i < days; i++) {
                // turn your data into Entry objects
                entries.add(new BarEntry(steps[i].getX(), steps[i].getY()));
            }
            no_steps_data_text.setVisibility(View.GONE);
            chart.setVisibility(View.VISIBLE);
        } else {
            isStepDataPresent = false;
            chart.setVisibility(View.GONE);
            no_steps_data_text.setVisibility(View.VISIBLE);
        }

        Log.e("ALLSTEPS", "Variable all steps: " + all_steps);
        BarDataSet dataSet = new BarDataSet(entries, "Last 7 days walked");

        if(((GlobalVar) getActivity().getApplication()).getNightMode()) {
            dataSet.setColor(Color.WHITE);
            dataSet.setValueTextColor(Color.WHITE);
        } else {
            dataSet.setColor(Color.BLACK);
            dataSet.setValueTextColor(Color.BLACK);
        }

        if(isStepDataPresent) {
            dataSet.setValueTextSize(15f);

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(0.5f);
            barData.setHighlightEnabled(false);

            chart.setData(barData);
            chart.setDragEnabled(false);
            chart.setTouchEnabled(false);
            chart.setScaleEnabled(false);
            chart.setPinchZoom(false);
            chart.setDoubleTapToZoomEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.getDescription().setEnabled(false);

            chart.getAxisLeft().setDrawGridLines(false);
            chart.getAxisLeft().setDrawAxisLine(false);
            chart.getAxisLeft().setDrawLabels(false);

            chart.getAxisRight().setDrawGridLines(false);
            chart.getAxisRight().setDrawAxisLine(false);
            chart.getAxisRight().setDrawLabels(false);

            if (((GlobalVar) getActivity().getApplication()).getNightMode()) {
                chart.getXAxis().setAxisLineColor(Color.WHITE);
                chart.getXAxis().setTextColor(Color.WHITE);
            } else {
                chart.getXAxis().setAxisLineColor(Color.BLACK);
                chart.getXAxis().setTextColor(Color.BLACK);
            }

            chart.getXAxis().setDrawGridLines(false);
            chart.getXAxis().setAxisLineWidth(2f);
            chart.getXAxis().setDrawAxisLine(true);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setTextSize(15f);

            chart.setExtraBottomOffset(10f);

            final ArrayList<String> xAxisLabel = new ArrayList<>();
            for (int i = 0; i < all_steps.size(); i++) {
                xAxisLabel.add(all_steps.get(i).get(0));
                Log.e("CHART xAxis lables", "Iteration: " + i);
            }
//        xAxisLabel.add(all_steps.get(0).get(0));
//        xAxisLabel.add(all_steps.get(1).get(0));
//        xAxisLabel.add(all_steps.get(2).get(0));
//        xAxisLabel.add("");
//        xAxisLabel.add("");
//        xAxisLabel.add("");
//        xAxisLabel.add("");

//        xAxisLabel.add(all_steps.get(3).get(0));
//        xAxisLabel.add(all_steps.get(4).get(0));
//        xAxisLabel.add(all_steps.get(5).get(0));
//        xAxisLabel.add(all_steps.get(6).get(0));

            chart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return xAxisLabel.get((int) value);
                }
            });

            chart.animateY(1000, Easing.EaseInOutCubic);
        }

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
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
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

    private void getFriends() {
        MySqlApi.getUserFriends(getContext());
    }
}