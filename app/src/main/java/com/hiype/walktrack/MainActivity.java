package com.hiype.walktrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.os.LocaleListCompat;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editTextEmail, editTextPassword;
    DBHelper db;
    SwitchCompat switch_toggle;
    LottieAnimationView lottieAnimationView;
    ProgressBar progressBar;
    TextView loginText;
    int nightMode;
    private Spinner language_select;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBHelper(this);

        if(db.doesUserExist()) {
            startActivity(new Intent(getApplicationContext(), Base.class));
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
            finish();
        }

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

        if(((GlobalVar) this.getApplication()).getLanguage() != null) {
            if (((GlobalVar) this.getApplication()).getLanguage().equals("Latvian")) {
                Log.e("MAINACTIVITY LANG", "Global var lang was Latvian");
                Locale locale = new Locale("lv");
                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                resources.updateConfiguration(configuration, displayMetrics);
                ((GlobalVar) getApplication()).setLanguage("Latvian");
            } else {
                Log.e("MAINACTIVITY LANG", "Global var lang was English");
                Locale locale = new Locale("en");
                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                resources.updateConfiguration(configuration, displayMetrics);
                ((GlobalVar) getApplication()).setLanguage("English");
            }
        } else {
            Log.e("MAINACTIVITY LANG", "Language global var was null");
            ((GlobalVar) getApplication()).setLanguage("English");
        }

        setContentView(R.layout.activity_main);

        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animationView);
        switch_toggle = (SwitchCompat) findViewById(R.id.night_switch);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        loginText = (TextView) findViewById(R.id.loginText);
        language_select = (Spinner) findViewById(R.id.language_spinner);

        if(((GlobalVar) this.getApplication()).getNightMode()) {
            Log.e("NIGHTMODE" , "Night mode ENABLED, setting lottie to night and changing toggle");
            lottieAnimationView.setAnimation(Lottie.LOTTIE_NIGHT);
            Log.e("LOTTIE MAIN ACTIVTY", "Lottie set to night");
            nightMode = 1;
            switch_toggle.setChecked(true);
        } else {
            Log.e("NIGHTMODE" , "Night mode DISABLED, setting lottie to night and changing toggle");
            lottieAnimationView.setAnimation(Lottie.LOTTIE_DAY);
            Log.e("LOTTIE MAIN ACTIVTY", "Lottie set to day");
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
            } else {
                language_select.setSelection(0, true);
                Log.e("MAINACTIVITY LANG", "Global var lang was English");
                Locale locale = new Locale("en");
                Resources resources = getResources();
                DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                Configuration configuration = resources.getConfiguration();
                configuration.setLocale(locale);
                resources.updateConfiguration(configuration, displayMetrics);
            }
        } else {
            language_select.setSelection(0);
        }

        language_select.setOnItemSelectedListener(this);

        editTextEmail = (EditText) findViewById(R.id.emailCodeBox);
        editTextPassword = (EditText) findViewById(R.id.newPasswordBox);

        switch_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int themeID;
                if (isChecked) {
                    Log.e("SWITCH", String.valueOf(isChecked));
                    ((GlobalVar) getApplication()).setNightMode(true);
                    lottieAnimationView.setAnimation(Lottie.LOTTIE_NIGHT);
                    Log.e("LOTTIE MAIN ACTIVITY", "Animation set to night!");
                    db.updateNightMode(true);

                } else {
                    Log.e("SWITCH", String.valueOf(isChecked));
                    ((GlobalVar) getApplication()).setNightMode(false);
                    lottieAnimationView.setAnimation(Lottie.LOTTIE_DAY);
                    Log.e("LOTTIE MAIN ACTIVITY", "Animation set to day!");
                    db.updateNightMode(false);
                }
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                finish();
                recreate();
            }
        });

        //if user presses on login
        //calling the method userLogin
        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("LOGIN", "Called the on click event");
                userLogin();
            }
        });

        //if user presses on not registered
        findViewById(R.id.backToLoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open register screen
                finish();
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        findViewById(R.id.forgotPasswordBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
                    if (!TextUtils.isEmpty(editTextEmail.getText().toString())) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                        intent.putExtra("EXTRA_EMAIL", editTextEmail.getText().toString());
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        startActivity(intent);
                    } else {
                        editTextEmail.setError(getString(R.string.PleaseEnterEmail));
                        editTextEmail.requestFocus();
                    }
                } else {
                    editTextEmail.setError("Please enter valid email");
                    editTextEmail.requestFocus();
                }
            }

        });
    }

    private void userLogin() {
        Log.e("LOGIN", "Login Function Called");
        //First getting the values
        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();
        Log.e("PASS", password);

        //Validating inputs
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError(getString(R.string.PleaseEnterEmail));
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email adress");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }

        //If everything is fine

        class UserLogin extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                Log.e("LOGIN", "Login Function Called PreExecute");
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
                loginText.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {

                Log.e("LOGINJSON", "Login Function called PostExecute");
                if (!TextUtils.isEmpty(s)) {
                    Log.e("LOGINJSON", "string is not empty");
                } else {Log.e("LOGINJSON", "string is empty");}
                super.onPostExecute(s);
                if (!TextUtils.isEmpty(s)) {
                    Log.e("LOGINJSON", "string is not empty");
                    Log.e("HTMLRecievedString", s);
                } else {Log.e("LOGINJSON", "string is empty");}
                progressBar.setVisibility(View.GONE);
                loginText.setVisibility(View.GONE);


                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        Log.e("Error Display","Inside Loop");
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //Getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //Creating a new user object
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("height"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("date_joined"),
                                userJson.getInt("stepCount"),
                                userJson.getString("friends_ids"),
                                userJson.getInt("iconID"),
                                userJson.getInt("has_desktop"),
                                userJson.getInt("totalSteps"),
                                nightMode,
                                userJson.getInt("points"),
                                userJson.getString("claimed_icons"),
                                userJson.getString("language")

                        );

                        if(!db.addUser(user)) {
                            db.resetDb();
                            db.addUser(user);
                        }

                        ((GlobalVar) getApplication()).setLanguage(userJson.getString("language"));

                        //Starting the profile activity
                        startActivity(new Intent(getApplicationContext(), Base.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //Creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //Creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("language", ((GlobalVar) getApplication()).getLanguage());

                //Returning the response of user login
                return requestHandler.sendPostRequest(WebServiceURL.URL_LOGIN, params);
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
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