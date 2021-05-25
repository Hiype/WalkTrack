package com.hiype.walktrack;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.hiype.walktrack.adapters.FriendsListAdapter;
import com.hiype.walktrack.adapters.UserListAdapter;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

public class MySqlApi {

    private final static String TAG = "MYSQL";

    public static void signInUser (String email, String password, Integer nightMode, Context context, Application application) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        ((GlobalVar) application).setExecutorService(executor);
        Handler handler = new Handler(Looper.getMainLooper());
        AtomicBoolean success = new AtomicBoolean(false);

        Log.e(TAG, "Started add user");
        DBHelper db = new DBHelper(context);

        //Background work
        executor.execute(() -> {

            //Creating request handler object
            RequestHandler requestHandler = new RequestHandler();

            //Creating request parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);

            String s = requestHandler.sendPostRequest(WebServiceURL.URL_LOGIN, params);

            handler.post(() -> {
                if (!TextUtils.isEmpty(s)) {
                    Log.e("HTMLRECEIVED", s);
                } else {
                    Log.e("HTMLRECEIVED", "Returned empty");
                }

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {

                        Log.e("Error Display","Inside Loop");
                        Toast.makeText( context, obj.getString("message"), Toast.LENGTH_SHORT).show();

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
                                0,
                                userJson.getString("claimed_icons"),
                                userJson.getString("language")

                        );

                        if(!db.addUser(user)) {
                            db.resetDb();
                            db.addUser(user);
                        }

                        success.set(true);

                        Log.e("HTMLRECEIVED", "User successfully logged in, success status: " + success.get());
                    } else {
                        Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        success.set(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("HTMLRECEIVED", e.getMessage());
                }

            });
        });
    }

    public static void updateUserData (String email, String password, String username, int height) {

//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        ((GlobalVar) application).setExecutorService(executor);
//        Handler handler = new Handler(Looper.getMainLooper());
//        AtomicBoolean success = new AtomicBoolean(false);
//
//        Log.e(TAG, "Started db update");
//
//        //Background work
//        executor.execute(() -> {
//
//            //Creating request handler object
//            RequestHandler requestHandler = new RequestHandler();
//
//            //Creating request parameters
//            HashMap<String, String> params = new HashMap<>();
//            params.put("email", email);
//            params.put("password", password);
//            params.put("username", username);
//            params.put("height", String.valueOf(height));
//
//            String s = requestHandler.sendPostRequest(WebServiceURL.URL_UPDATEDB, params);
//
//            handler.post(() -> {
//
//            });
//        });

        final OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("username", username)
                .add("height", String.valueOf(height))
                .build();
        Request request = new Request.Builder()
                .url(WebServiceURL.URL_UPDATE)
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
                    Log.e("OKHTTP UPDATE DATA RESP", "Code: " + String.valueOf(response.code()) + " String: " + response.body().string());
                } else {
                    Log.e("OKHTTP UPDATE DATA RESP", "Code: " + response.code());
                }
            }
        });

    }

    public static void updateUserSteps (ArrayList<Integer> stepsAndPointsArray, Context context, Application application) {

//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        ((GlobalVar) application).setExecutorService(executor);
//        Handler handler = new Handler(Looper.getMainLooper());
//        AtomicBoolean success = new AtomicBoolean(false);
//
//        Log.e(TAG, "Started db update");
//        DBHelper db = new DBHelper(context);
//
//        //Background work
//        executor.execute(() -> {
//
//            //Creating request handler object
//            RequestHandler requestHandler = new RequestHandler();
//
//            //Creating request parameters
//            HashMap<String, String> params = new HashMap<>();
//            params.put("user_id", String.valueOf(db.getCurrentUserID()));
//            params.put("todaySteps", String.valueOf(stepsAndPointsArray.get(0)));
//            params.put("totalSteps", String.valueOf(stepsAndPointsArray.get(1)));
//            params.put("points", String.valueOf(stepsAndPointsArray.get(2)));
//
//            String s = requestHandler.sendPostRequest(WebServiceURL.URL_UPDATEDB, params);
//
//            handler.post(() -> {
//
//            });
//        });

        final OkHttpClient client = new OkHttpClient();
        DBHelper db = new DBHelper(context);

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(db.getCurrentUserID()))
                .add("todaySteps", String.valueOf(stepsAndPointsArray.get(0)))
                .add("totalSteps", String.valueOf(stepsAndPointsArray.get(1)))
                .add("points", String.valueOf(stepsAndPointsArray.get(2)))
                .build();
        Request request = new Request.Builder()
                .url(WebServiceURL.URL_UPDATEDB)
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
                    Log.e("OKHTTP UPDATE STEPS RESP", "Code: " + String.valueOf(response.code()) + " String: " + response.body().string());
                } else {
                    Log.e("OKHTTP UPDATE STEPS RESP", "Code: " + response.code());
                }
            }
        });
    }

    public static void getUserFriends (Context context) {
        final OkHttpClient client = new OkHttpClient();
        DBHelper db = new DBHelper(context);

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(db.getCurrentUserID()))
                .build();
        Request request = new Request.Builder()
                .url(WebServiceURL.URL_GETFRIENDS)
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
                    Log.e("OKHTTP GET FRIENDS RESP", "Code: " + String.valueOf(response.code()) + " String: " + response.body().string());
                } else {
                    Log.e("OKHTTP GET FRIENDS RESP", "Code: " + response.code());
                }
            }
        });
    }

    public static JSONObject removeFriend (int friend_id, Context context) {
        final OkHttpClient client = new OkHttpClient();
        final JSONObject[] obj = new JSONObject[1];
        DBHelper db = new DBHelper(context);

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(db.getCurrentUserID()))
                .add("removed_friend", String.valueOf(friend_id))
                .build();
        Request request = new Request.Builder()
                .url(WebServiceURL.URL_REMOVE_FRIEND)
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
                    Log.e("OKHTTP GET FRIENDS RESP", "Code: " + String.valueOf(response.code()) + " String: " + response.body().string());
                    try {
                        obj[0] = new JSONObject(response.body().string());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("OKHTTP GET FRIENDS RESP", "Code: " + response.code());
                }
            }
        });
        return obj[0];
    }

    public static void addClaimedIcon (int claimed_icon, Context context) {
        DBHelper db = new DBHelper(context);
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        DBHelper db = new DBHelper(context);
//
//        //Background work
//        executor.execute(() -> {
//
//            //Creating request handler object
//            RequestHandler requestHandler = new RequestHandler();
//            int user_id = db.getCurrentUserID();
//
//            //Creating request parameters
//            HashMap<String, String> params = new HashMap<>();
//            params.put("user_id", String.valueOf(user_id));
//            params.put("claimed_icon_int", String.valueOf(claimed_icon));
//
//            Log.e(TAG, "Passing variables to ext db, user_id: " + String.valueOf(user_id) + ", claimed_icon_int: " + String.valueOf(claimed_icon));
//
//            String s = requestHandler.sendPostRequest(WebServiceURL.URL_ADDCLAIMEDICON, params);
//            Log.e("ADDCLAIMICON EXTERNAL", "Added icon sent, answer: " + s);
//
//            handler.post(() -> {
//
//            });
//        });
        final OkHttpClient client = new OkHttpClient();
        int user_id = db.getCurrentUserID();

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("claimed_icon_int", String.valueOf(claimed_icon))
                .build();
        Request request = new Request.Builder()
                .url(WebServiceURL.URL_ADDCLAIMEDICON)
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
                    Log.e("OKHTTP SET ADDICON RESP", "Code: " + String.valueOf(response.code()) + " String: " + response.body().string());
                } else {
                    Log.e("OKHTTP SET ADDICON RESP", "Code: " + response.code());
                }
            }
        });
    }

    public static ArrayList<FriendUser> findFriend (Context context) {
        final OkHttpClient client = new OkHttpClient();
        ArrayList<FriendUser> searchResultArray = new ArrayList<>();
        DBHelper db = new DBHelper(context);
        int user_id = db.getCurrentUserID();

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .build();
        Request request = new Request.Builder()
                .url(WebServiceURL.URL_SEARCH_FRIENDS)
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
                String responseString = response.body().string();
                if (response.isSuccessful()) {
                    Log.e("OKHTTP FIND FRIEND RESP", "Code: " + String.valueOf(response.code()) + " String: " + responseString);
                    try {
                        JSONObject obj = new JSONObject(responseString);

                        ArrayList<FriendUser> searchResultArray = new ArrayList<>();
                        try {
                            if (!obj.getBoolean("error")) {
                                for(int i = 0; i < obj.getInt("friend_count"); i++) {
                                    JSONObject userJson = obj.getJSONObject("user" + i);

                                    FriendUser friendUser = new FriendUser(
                                            userJson.getInt("id"),
                                            userJson.getInt("iconID"),
                                            userJson.getString("username")
                                    );

                                    Log.e("Friends add iterator", "Iteration: " + i);

                                    searchResultArray.add(friendUser);
                                }
                                Log.e("USER SEARCH", "Result array: " + Arrays.toString(searchResultArray.toArray()));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("OKHTTP FIND FRIEND RESP", "Code: " + response.code());
                }
            }
        });
        return searchResultArray;
    }

    public static ArrayList<FriendUser> findUser (String input_username, Context context) {
        final OkHttpClient client = new OkHttpClient();
        ArrayList<FriendUser> searchResultArray = new ArrayList<>();

        RequestBody formBody = new FormBody.Builder()
                .add("input_username", input_username)
                .build();
        Request request = new Request.Builder()
                .url(WebServiceURL.URL_SEARCH_USERS)
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
                    try {
                        DBHelper db = new DBHelper(context);
                        JSONObject obj = new JSONObject(response.body().string());

                        try {
                            if (!obj.getBoolean("error")) {
                                for(int i = 1; i < obj.getInt("user_count") + 1; i++) {
                                    JSONObject userJson = obj.getJSONObject("user" + i);

                                    ArrayList<String> dbFriends = db.getFriends();
                                    boolean isFriend = false;

                                    Log.e("Friend add action", "db friends size: " + dbFriends.size());

                                    for(int j = 0; j < dbFriends.size(); j++) {

                                        if(dbFriends.get(j).equals(userJson.getString("id"))) {
                                            isFriend = true;
                                            Log.e("Is friend check loop", dbFriends.get(j) + " is friend with id " + userJson.getString("id"));
                                            break;
                                        }
                                    }

                                    Log.e("Friend add action", userJson.getString("username") + " IsFriend: " + isFriend);

                                    if(!isFriend && db.getCurrentUserID() != userJson.getInt("id")) {

                                        FriendUser matchedUser = new FriendUser(
                                                userJson.getInt("id"),
                                                userJson.getInt("iconID"),
                                                userJson.getString("username")
                                        );

                                        Log.e("Friends add iterator", "Iteration: " + i);

                                        searchResultArray.add(matchedUser);
                                    }
                                }
                                Log.e("USER SEARCH", "Result array: " + Arrays.toString(searchResultArray.toArray()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("OKHTTP FIND USER RESP", "Code: " + response.code());
                }
            }
        });
        return searchResultArray;
    }

    public static void updateLanguage (Application application) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        DBHelper db = new DBHelper(application.getBaseContext());

        //Background work
        executor.execute(() -> {

            //Creating request handler object
            RequestHandler requestHandler = new RequestHandler();
            int user_id = db.getCurrentUserID();

            //Creating request parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("user_id", String.valueOf(user_id));
            params.put("language", ((GlobalVar) application).getLanguage());

            String s = requestHandler.sendPostRequest(WebServiceURL.URL_SET_LANGUAGE, params);
            Log.e("SETLANGUAGE EXTERNAL", "Added icon sent, answer: " + s);

            handler.post(() -> {

            });
        });
    }

    public static void setIconID (Context context, int icon_id) {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
        DBHelper db = new DBHelper(context);
//
//        //Background work
//        executor.execute(() -> {
//
//            //Creating request handler object
//            RequestHandler requestHandler = new RequestHandler();
//            int user_id = db.getCurrentUserID();
//
//            //Creating request parameters
//            HashMap<String, String> params = new HashMap<>();
//            params.put("user_id", String.valueOf(user_id));
//            params.put("icon_id", String.valueOf(icon_id));
//
//            String s = requestHandler.sendPostRequest(WebServiceURL.URL_SET_ICONID, params);
//            Log.e("SETLANGUAGE EXTERNAL", "Added icon sent, answer: " + s);
//
//            handler.post(() -> {
//
//            });
//        });

        final OkHttpClient client = new OkHttpClient();
        int user_id = db.getCurrentUserID();

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("icon_id", String.valueOf(icon_id))
                .build();
        Request request = new Request.Builder()
                .url(WebServiceURL.URL_SET_ICONID)
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
                    Log.e("OKHTTP SET ICONID RESP", "Code: " + String.valueOf(response.code()) + " String: " + response.body().string());
                } else {
                    Log.e("OKHTTP SET ICONID RESP", "Code: " + response.code());
                }
            }
        });
    }
}
