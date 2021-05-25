package com.hiype.walktrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.hiype.walktrack.adapters.ImageAdapter;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText editTextEmail, editTextPassword, editTextUsername;
    private NumberPicker numberPickerHeight;
    private DBHelper db;
    private LottieAnimationView lottieAnimationView;
    int nightMode;
    private SwitchCompat switch_toggle;
    private String lang;
    private Spinner language_select;
    private GifImageView iconSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        if(((GlobalVar) this.getApplication()).getLanguage().equals("Latvian")) {
            Locale locale = new Locale("lv");
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, displayMetrics);
            ((GlobalVar) getApplication()).setLanguage("Latvian");
            lang = "Latvian";
        } else {
            Locale locale = new Locale("en");
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, displayMetrics);
            ((GlobalVar) getApplication()).setLanguage("English");
            lang = "English";
        }

        setContentView(R.layout.signupactivity);
        db = new DBHelper(this);

        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animationView);
        switch_toggle = (SwitchCompat) findViewById(R.id.night_switch);
        language_select = (Spinner) findViewById(R.id.language_spinner_signup);
        iconSelect = (GifImageView) findViewById(R.id.iconSelect);

        NumberPicker np = findViewById(R.id.numberPicker);

        np.setMinValue(140);
        np.setMaxValue(250);
        np.setValue(170);

        np.setOnValueChangedListener(onValueChangeListener);
        np.setOnScrollListener(onScrollListener);

        if(((GlobalVar) this.getApplication()).getNightMode()) {
            lottieAnimationView.setAnimation(Lottie.LOTTIE_NIGHT);
            Log.e("LOTTIE SIGNUP", "Lottie set to NIGHT");
            nightMode = 1;
            switch_toggle.setChecked(true);
        } else {
            lottieAnimationView.setAnimation(Lottie.LOTTIE_DAY);
            Log.e("LOTTIE SIGNUP", "Lottie set to DAY");
            nightMode = 0;
            switch_toggle.setChecked(false);
        }

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language_select.setAdapter(adapter);

        if(((GlobalVar) this.getApplication()).getLanguage() != null) {
            if (((GlobalVar) this.getApplication()).getLanguage().equals("Latvian")) {
                language_select.setSelection(1, true);
                Log.e("MAINACTIVITY LANG", "Global var lang was Latvian");
                Locale locale = new Locale("lv");
                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                resources.updateConfiguration(configuration, displayMetrics);
                lang = "Latvian";
            } else {
                language_select.setSelection(0, true);
                Log.e("MAINACTIVITY LANG", "Global var lang was English");
                Locale locale = new Locale("en");
                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                resources.updateConfiguration(configuration, displayMetrics);
                lang = "English";
            }
        } else {
            language_select.setSelection(0);
        }

        language_select.setOnItemSelectedListener(this);

        switch_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    Log.e("SWITCH", String.valueOf(isChecked));
                    ((GlobalVar) getApplication()).setNightMode(true);
                    lottieAnimationView.setAnimation(Lottie.LOTTIE_NIGHT);
                    Log.e("LOTTIE SIGNUP", "Lottie set to NIGHT");
                    db.updateNightMode(true);

                } else {
                    Log.e("SWITCH", String.valueOf(isChecked));
                    ((GlobalVar) getApplication()).setNightMode(false);
                    lottieAnimationView.setAnimation(Lottie.LOTTIE_DAY);
                    Log.e("LOTTIE SIGNUP", "Lottie set to DAY");
                    db.updateNightMode(false);
                }
                recreate();
            }
        });

        //if user presses on back to login
        findViewById(R.id.backToLoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open login screen
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        iconSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageAdapter imageAdapter = new ImageAdapter(getBaseContext());

                View view = getWindow().getDecorView().getRootView();

                LayoutInflater inflater = getLayoutInflater();
                try {
                    view = inflater.inflate(R.layout.dialog_icon_select, null);
                } catch (InflateException e) { e.printStackTrace(); }

                GridView gridview = (GridView) view.findViewById(R.id.icon_select_grid);

                try {
                    gridview.setAdapter(imageAdapter);
                } catch (Exception e) { e.printStackTrace(); }

                final AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setTitle("Icon select");
                builder.setView(view);
                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setCancelable(true);
                builder.create().show();

            }
        });

        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Register user
                Log.e("REGISTRATION", "Called the on click event");
                userRegister();
            }
        });

    }

    NumberPicker.OnValueChangeListener onValueChangeListener =
            new 	NumberPicker.OnValueChangeListener(){
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    Toast.makeText(SignupActivity.this,
                            "selected number "+numberPicker.getValue(), Toast.LENGTH_SHORT);
                }
            };

    NumberPicker.OnScrollListener onScrollListener =
            new NumberPicker.OnScrollListener() {
                @Override
                public void onScrollStateChange(NumberPicker view, int scrollState) {
                    ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
                }
            };



    private void userRegister() {
        editTextEmail = (EditText) findViewById(R.id.emailCodeBox);
        editTextPassword = (EditText) findViewById(R.id.newPasswordBox);
        editTextUsername = (EditText) findViewById(R.id.usernameBox);
        numberPickerHeight = (NumberPicker) findViewById(R.id.numberPicker);

        Log.e("REGISTRATION", "Register Function Called");
        //First getting the values
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        final String username = editTextUsername.getText().toString();
        final String height = Integer.toString(numberPickerHeight.getValue());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        final String date = dtf.format(now);
        Log.e("PASS", password);
        Log.e("DATE", date);

        //Validating inputs
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.PleaseEnterEmail));
            editTextEmail.requestFocus();
            return;
        }

        //Checks if password is empty and highlights it, but if its not empty but its length is smaller than 3 then highlights that
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        } else {
            if (password.length() < 3) {
                editTextPassword.setError(getString(R.string.password_needs_to_be_longer));
                editTextPassword.requestFocus();
                return;
            }
        }

        //Checks if username is empty and highlights it, but if its not empty but its length is smaller than 3 then highlights that
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Please enter your username");
            editTextUsername.requestFocus();
            return;
        } else {
            if (username.length() < 3) {
                editTextUsername.setError(getString(R.string.username_needs_to_be_longer));
                editTextUsername.requestFocus();
                return;
            }
        }

        //If everything is fine
        class UserRegister extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                Log.e("REGISTRATION", "Register Function Called PreExecute");
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {

                Log.e("REGISTERJSON", "Register Function called PostExecute");
                if (!TextUtils.isEmpty(s)) {
                    Log.e("REGISTERJSON", "string is not empty");
                } else {Log.e("REGISTERJSON", "string is empty");}
                super.onPostExecute(s);
                if (!TextUtils.isEmpty(s)) {
                    Log.e("REGISTERJSON", "string is not empty");
                    Log.e("HTMLRecievedString", s);
                } else {Log.e("REGISTERJSON", "string is empty");}

                if(!TextUtils.isEmpty(s)) {
                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(s);
                        //if no error in response
                        if (!obj.getBoolean("error")) {

                            Log.e("Error Display", "Inside Loop");
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            //Getting the user from the response
                            JSONObject userJson = obj.getJSONObject("user");

                            //Creating a new user object
                            User user = new User(
                                    userJson.getInt("id"),
                                    userJson.getString("height"),
                                    userJson.getString("date_joined"),
                                    userJson.getString("username"),
                                    userJson.getString("email"),
                                    userJson.getInt("stepCount"),
                                    null,
                                    userJson.getInt("iconID"),
                                    0,
                                    userJson.getInt("totalSteps"),
                                    nightMode,
                                    0,
                                    "1, 2, 3, 4, 5, 6",
                                    lang

                            );

                            //Storing the user in local database
                            db.addUser(user);

                            ((GlobalVar) getApplication()).setLanguage(lang);

                            //Starting the profile activity
                            startActivity(new Intent(getApplicationContext(), Base.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();

                        } else {
                            Toast.makeText(getApplicationContext(), "User with this username/email already exists", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("JSONCREATION", "Failed to create json object from string");
                        e.printStackTrace();
                    }
                } else {
                    JSONObject obj = new JSONObject();

                    User user = new User(
                            0,
                            height,
                            username,
                            email,
                            date,
                            0,
                            null,
                            1,
                            0,
                            0,
                            nightMode,
                            0,
                            "1, 2, 3, 4, 5, 6",
                            lang
                    );

                    db.addUser(user);

                    ((GlobalVar) getApplication()).setLanguage(lang);

                    //Starting the profile activity
                    startActivity(new Intent(getApplicationContext(), Base.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //Creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //Creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("username", username);
                params.put("password", password);
                params.put("height", height);
                params.put("date_joined", date);
                params.put("language", lang);

                //returning the Response of user login
                return requestHandler.sendPostRequest(WebServiceURL.URL_REGISTER, params);
            }
        }
        UserRegister ur = new UserRegister();
        ur.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Locale locale;
        Log.e("LANGUAGE CHANGER", "onItemSelected called, currentLanguage: " + getResources().getConfiguration().getLocales().get(0).getDisplayLanguage());
        if(position == 0) {
            if(getResources().getConfiguration().getLocales().get(0).getDisplayLanguage().equals("English")) {
                Log.e("LANGUAGE CHANGE", "EN language is already on display!");

            } else {
                locale = new Locale("en");
                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                resources.updateConfiguration(configuration, displayMetrics);
                ((GlobalVar) getApplication()).setLanguage("English");
                recreate();
            }

        } else {
            if(getResources().getConfiguration().getLocales().get(0).getDisplayLanguage().equals("Latvian")) {
                Log.e("LANGUAGE CHANGE", "LV language is already on display!");
            } else {
                locale = new Locale("lv");
                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                resources.updateConfiguration(configuration, displayMetrics);
                ((GlobalVar) getApplication()).setLanguage("Latvian");
                recreate();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}