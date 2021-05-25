package com.hiype.walktrack.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.hiype.walktrack.Base;
import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.DialogIconSelect;
import com.hiype.walktrack.GlobalVar;
import com.hiype.walktrack.IconPickerPopup;
import com.hiype.walktrack.Lottie;
import com.hiype.walktrack.MainActivity;
import com.hiype.walktrack.MySqlApi;
import com.hiype.walktrack.R;
import com.hiype.walktrack.User;
import com.hiype.walktrack.adapters.ImageAdapter;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;
import com.hiype.walktrack.service.StepCounterService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ImageView settingsNavigationButton, friendsNavigationButton;
    private GifImageView user_icon_select;
    private Button logout_btn;
    private DrawerLayout drawerLayout;
    private DBHelper db;
    private TextView profile_username, profile_join_date;
    private EditText profile_username_input, profile_email_input, profile_password_input;
    private NumberPicker height_picker;
    private Button save_changes_button;
    private Switch night_switch;
    private LottieAnimationView lottieAnimationView;
    private NavigationView navigationView;
    private MenuItem english_item;
    private MenuItem latvian_item;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        db = new DBHelper(getContext());

        User user = db.getUser();

        if(getView() != null) {
            settingsNavigationButton = (ImageView) view.findViewById(R.id.settingsNavigationButton);
            user_icon_select = (GifImageView) view.findViewById(R.id.user_icon_select);

            save_changes_button = (Button) view.findViewById(R.id.save_changes_button);
            logout_btn = (Button) view.findViewById(R.id.log_out_button);

            drawerLayout = (DrawerLayout) view.findViewById(R.id.settingsNavigation);
            height_picker = (NumberPicker) view.findViewById(R.id.numberPicker2);

            profile_username = (TextView) view.findViewById(R.id.profile_username);
            profile_join_date = (TextView) view.findViewById(R.id.profile_join_date);

            profile_username_input = (EditText) view.findViewById(R.id.profile_username_input);
            profile_email_input = (EditText) view.findViewById(R.id.profile_email_input);
            profile_password_input = (EditText) view.findViewById(R.id.profile_password_input);

            navigationView = (NavigationView) view.findViewById(R.id.settings_navigation_view);

        }

        lottieAnimationView = (LottieAnimationView) getActivity().findViewById(R.id.animationView);
        navigationView.setItemIconTintList(null);

        //Upadating the user icon
        try {
            Class res = R.drawable.class;
            Field field;
            field = res.getField("icon" + db.getIcon());
            int drawableId = field.getInt(null);
            user_icon_select.setImageResource(drawableId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

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

        String username = user.getUsername();

        profile_username.setText(username);
        profile_join_date.setText(user.getDate_joined());

        profile_username_input.setText(username);
        profile_email_input.setText(user.getEmail());

            height_picker.setMinValue(140);
            height_picker.setMaxValue(250);
            height_picker.setValue(Integer.parseInt(user.getHeight()));

            height_picker.setOnValueChangedListener(onValueChangeListener);
            height_picker.setOnScrollListener(onScrollListener);

            save_changes_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userUpdate();
                }
            });

            profile_email_input.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), getResources().getText(R.string.email_cant_be_changed), Toast.LENGTH_SHORT).show();
                }
            });

            user_icon_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentIcon = db.getIcon();
                    View view = v;
                    ImageAdapter imageAdapter = new ImageAdapter(getContext());

                    LayoutInflater inflater = requireActivity().getLayoutInflater();
                    try {
                        view = inflater.inflate(R.layout.dialog_icon_select, null);
                    } catch (InflateException e) { e.printStackTrace(); }

                    GridView gridview = (GridView) view.findViewById(R.id.icon_select_grid);

                    try {
                        gridview.setAdapter(imageAdapter);
                    } catch (Exception e) { e.printStackTrace(); }

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Icon select");
                    builder.setView(view);
                    builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int newIcon = db.getIcon();
                            MySqlApi.setIconID(getContext(), newIcon);

                            Class res = R.drawable.class;
                            Field field;

                            try {
                                field = res.getField("icon" + newIcon);
                                int drawableId = field.getInt(null);
                                user_icon_select.setImageResource(drawableId);
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }

                        }
                    }).setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.setIcon(currentIcon);
                        }
                    });
                    builder.setCancelable(false);
                    builder.create().show();

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

            logout_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteUser();
                    getActivity().stopService(new Intent( getContext(), StepCounterService.class));
                    startActivity(new Intent(getContext(), MainActivity.class));
                    getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    getActivity().finish();
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

    NumberPicker.OnValueChangeListener onValueChangeListener =
            new NumberPicker.OnValueChangeListener(){
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    Toast.makeText(getContext(),
                            "selected number "+numberPicker.getValue(), Toast.LENGTH_SHORT);
                }
            };

    NumberPicker.OnScrollListener onScrollListener =
            new NumberPicker.OnScrollListener() {
                @Override
                public void onScrollStateChange(NumberPicker view, int scrollState) {
                    ((Vibrator) getContext().getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
                }
            };


//    private void getUser() {
//
//        Log.e("UPDATE", "UPDATE Function Called");
//        //First getting the values
//
//        final String received_email = profile_email_input.getText().toString();
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDateTime now = LocalDateTime.now();
//        final String date = dtf.format(now);
//        Log.e("PASS", received_password);
//        Log.e("DATE", date);
//
//        //If everything is fine
//        class GetUser extends AsyncTask<Void, Void, String> {
//
//            @Override
//            protected void onPreExecute() {
//                Log.e("GETUSER", "GETUSER Function Called PreExecute");
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//
//                Log.e("GETJSON", "GETUSER Function called PostExecute");
//                if (!TextUtils.isEmpty(s)) {
//                    Log.e("GETJSON", "string is not empty");
//                } else {Log.e("GETJSON", "string is empty");}
//                super.onPostExecute(s);
//                if (!TextUtils.isEmpty(s)) {
//                    Log.e("GETJSON", "string is not empty");
//                    Log.e("HTMLRecievedString", s);
//                } else {Log.e("GETJSON", "string is empty");}
//
//                if(!TextUtils.isEmpty(s)) {
//                    try {
//                        //converting response to json object
//                        JSONObject obj = new JSONObject(s);
//                        //if no error in response
//                        if (!obj.getBoolean("error")) {
//
//                            Log.e("Error Display", "Inside Loop");
//                            Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
//
//                            //Getting the user from the response
//                            JSONObject userJson = obj.getJSONObject("user");
//
//                            //Creating a new user object
//                            user = new User(
//                                    userJson.getInt("id"),
//                                    userJson.getString("height"),
//                                    userJson.getString("date_joined"),
//                                    userJson.getString("username"),
//                                    userJson.getString("email"),
//                                    userJson.getInt("stepCount"),
//                                    null,
//                                    userJson.getInt("iconID"),
//                                    userJson.getInt("has_desktop"),
//                                    userJson.getInt("totalSteps")
//                            );
//
//
//                        } else {
//                            Toast.makeText(getContext(), "User with this username/email already exists", Toast.LENGTH_LONG).show();
//                        }
//                    } catch (JSONException e) {
//                        Log.e("JSONCREATION", "Failed to create json object from string");
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            protected String doInBackground(Void... voids) {
//                //Creating request handler object
//                RequestHandler requestHandler = new RequestHandler();
//
//                //Creating request parameters
//                HashMap<String, String> params = new HashMap<>();
//                params.put("email", received_email);
//
//                //returning the Response of user login
//                return requestHandler.sendPostRequest(WebServiceURL.URL_UPDATE, params);
//            }
//        }
//        GetUser gu = new GetUser();
//        gu.execute();
//    }

    private void userUpdate() {

        Log.e("UPDATE", "UPDATE Function Called");
        //First getting the values
        final String received_username = profile_username_input.getText().toString();
        final String received_password = profile_password_input.getText().toString();
        final String received_email = profile_email_input.getText().toString();
        final Integer received_height = height_picker.getValue();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        final String date = dtf.format(now);
        Log.e("PASS", received_password);
        Log.e("DATE", date);
        boolean updatePassword = false, updateUsername = false;

        //Deciding what to update
        if (!TextUtils.isEmpty(received_username)) {
            updateUsername = true;
        }
        if (!TextUtils.isEmpty(received_password)) {
            updatePassword = true;
        }

        //If everything is fine
        boolean finalUpdateUsername = updateUsername;
        boolean finalUpdatePassword = updatePassword;

        MySqlApi.updateUserData(received_email, received_password, received_username, received_height);
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