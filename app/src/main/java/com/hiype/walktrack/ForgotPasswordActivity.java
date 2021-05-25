package com.hiype.walktrack;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

public class ForgotPasswordActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    DBHelper db;
    int nightMode;
    LottieAnimationView lottieAnimationView;
    SwitchCompat switch_toggle;
    private String lang;
    private Spinner language_select;

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
        } else {
            Locale locale = new Locale("en");
            Resources resources = getResources();
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration, displayMetrics);
            ((GlobalVar) getApplication()).setLanguage("English");
        }

        setContentView(R.layout.forgotpasswordactivity);

        db = new DBHelper(this);

        //Gets email from previous activity
        String recoveryEmail = getIntent().getStringExtra("EXTRA_EMAIL");

        TextView timeCounter = (TextView) findViewById(R.id.timeCounter);
        EditText enteredEmailCode = (EditText) findViewById(R.id.emailCodeBox);
        EditText enteredPassword = (EditText) findViewById(R.id.newPasswordBox);

        Timer timer = new Timer(timeCounter);
        RandomString secureCode = new RandomString();
        secureCode.nextString();

        Log.e("Email code gen", "generated code: " + secureCode.getCurrentString());

        sendMail(recoveryEmail, secureCode.getCurrentString());

        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animationView);
        switch_toggle = (SwitchCompat) findViewById(R.id.night_switch);
        language_select = (Spinner) findViewById(R.id.language_spinner_forgot_password);

        if(((GlobalVar) this.getApplication()).getNightMode()) {
            lottieAnimationView.setAnimation(Lottie.LOTTIE_NIGHT);
            Log.e("LOTTIE FORGOTPASS", "Lottie set to NIGHT");
            nightMode = 1;
            switch_toggle.setChecked(true);
        } else {
            lottieAnimationView.setAnimation(Lottie.LOTTIE_DAY);
            Log.e("LOTTIE FORGOTPASS", "Lottie set to DAY");
            nightMode = 0;
            switch_toggle.setChecked(false);
        }

        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e) {
            Log.e("ACTIONBAR", "Unable to hide action bar");
        }

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

        switch_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Log.e("SWITCH", String.valueOf(isChecked));
                    ((GlobalVar) getApplication()).setNightMode(true);
                    lottieAnimationView.setAnimation(Lottie.LOTTIE_NIGHT);
                    Log.e("LOTTIE FORGOTPASS", "Lottie set to NIGHT");
                    db.updateNightMode(true);

                } else {
                    Log.e("SWITCH", String.valueOf(isChecked));
                    ((GlobalVar) getApplication()).setNightMode(false);
                    lottieAnimationView.setAnimation(Lottie.LOTTIE_DAY);
                    Log.e("LOTTIE FORGOTPASS", "Lottie set to DAY");
                    db.updateNightMode(false);
                }

                Log.e("RECREATE", "Recreating activity");
                recreate();
            }
        });

        findViewById(R.id.backToLoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open login screen
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String emailCode = enteredEmailCode.getText().toString();
                final String password = enteredPassword.getText().toString();

                if(TextUtils.isEmpty(emailCode)) {
                    enteredEmailCode.setError(getString(R.string.please_enter_email_code));
                    enteredEmailCode.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(password)) {
                    enteredPassword.setError(getString(R.string.please_enter_password));
                    enteredPassword.requestFocus();
                    return;
                } else {
                    if(password.length() < 3) {
                        enteredPassword.setError(getString(R.string.password_needs_to_be_longer));
                        enteredPassword.requestFocus();
                        return;
                    }
                }

                //Login user
                if(emailCode.equals(secureCode.getCurrentString()) && timer.isCounting()) {
                    Log.e("Code comparison",emailCode + " is equal to " + secureCode.getCurrentString());
                    userPasswordUpdate(password, recoveryEmail, secureCode.getCurrentString());
                } else {
                    Log.e("Code comparison",emailCode + " is not equal to " + secureCode.getCurrentString() + " is timer counting: " + timer.isCounting());
                    Toast.makeText(ForgotPasswordActivity.this, "The entered code is invalid!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.SendNewCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secureCode.nextString();
                sendMail(recoveryEmail, secureCode.getCurrentString());
                timer.cdt.start();
                Toast.makeText(ForgotPasswordActivity.this, "New email has been sent", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMail(String email, String emailCode) {
        Log.e("SENDMAIL", "Password update Function Called");

        class SendMailTask extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {

                Log.e("PASSUPDATEJSON", "Login Function called PostExecute");
                if (!TextUtils.isEmpty(s)) {
                    Log.e("PASSUPDATEJSON", "string is not empty");
                } else {Log.e("PASSUPDATEJSON", "string is empty");}
                super.onPostExecute(s);
                if (!TextUtils.isEmpty(s)) {
                    Log.e("PASSUPDATEJSON", "string is not empty");
                    Log.e("HTMLRecievedString", s);
                } else {Log.e("PASSUPDATEJSON", "string is empty");}


                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        Log.e("Error Display","Inside Loop");
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "This user does not exist", Toast.LENGTH_SHORT).show();
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
                params.put("emailCode", emailCode);

                //returning the Response of user login
                return requestHandler.sendPostRequest(WebServiceURL.URL_FORGOTPASSWORD, params);
            }
        }
        SendMailTask smt = new SendMailTask();
        smt.execute();
    }

    private void userPasswordUpdate(String password, String email, String emailCode) {
        Log.e("PASSUPDATE", "Password update Function Called");
        Log.e("PASS", password);

        class PassUpdate extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {

                Log.e("PASSUPDATEJSON", "Login Function called PostExecute");
                if (!TextUtils.isEmpty(s)) {
                    Log.e("PASSUPDATEJSON", "string is not empty");
                } else {Log.e("PASSUPDATEJSON", "string is empty");}
                super.onPostExecute(s);
                if (!TextUtils.isEmpty(s)) {
                    Log.e("PASSUPDATEJSON", "string is not empty");
                    Log.e("HTMLRecievedString", s);
                } else {Log.e("PASSUPDATEJSON", "string is empty");}

                if(!TextUtils.isEmpty(s)) {
                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(s);
                        //if no error in response
                        if (!obj.getBoolean("error")) {

                            Log.e("Error Display", "Inside Loop");

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

                            db.addUser(user);

                            ((GlobalVar) getApplication()).setLanguage(userJson.getString("language"));

                            //Starting the profile activity
                            startActivity(new Intent(getApplicationContext(), Base.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Database error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                params.put("emailCode", emailCode);

                //returning the Response of user login
                return requestHandler.sendPostRequest(WebServiceURL.URL_RESETPASSWORD, params);
            }
        }
        PassUpdate pu = new PassUpdate();
        pu.execute();
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
                recreate();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
