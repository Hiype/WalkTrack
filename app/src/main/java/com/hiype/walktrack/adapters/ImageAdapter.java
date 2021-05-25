package com.hiype.walktrack.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.MySqlApi;
import com.hiype.walktrack.R;
import com.hiype.walktrack.http.WebServiceURL;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<Integer> icons;

    // Constructor
    public ImageAdapter(Context c) {
        mContext = c;
        DBHelper db = new DBHelper(c);
        icons = db.getClaimedIcons();

        if(icons == null || icons.isEmpty()) {
            icons = new ArrayList<Integer>();
            for(int i = 1; i < 7; i++) {
                icons.add(i);
            }
        }
        Log.e("IMAGE ADAPTER", "icon array: " + Arrays.toString(icons.toArray()));
    }

    public int getCount() {
        return icons.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        GifImageView imageView;
        String name = "icon";

        if (convertView == null) {
            imageView = new GifImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(5, 5, 5, 5);
        }
        else
        {
            imageView = (GifImageView) convertView;
        }

        Class res = R.drawable.class;
        Field field;

        try {
            field = res.getField(name + icons.get(position));
            int drawableId = field.getInt(null);
            imageView.setImageResource(drawableId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        imageView.setClickable(true);

        final boolean[] selected = {false};

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

                if (selected[0]) {
                    imageView.setBackgroundColor(Color.TRANSPARENT);
                    selected[0] = false;

                } else {
                    imageView.setBackgroundColor(Color.BLUE);
                    selected[0] = true;
                }

                DBHelper db = new DBHelper(v.getContext());
                db.setIcon(icons.get(position));

//                int user_id = db.getCurrentUserID();
//
//                final OkHttpClient client = new OkHttpClient();
//
//                RequestBody formBody = new FormBody.Builder()
//                        .add("user_id", String.valueOf(user_id))
//                        .add("icon_id", String.valueOf(icons.get(position)))
//                        .build();
//                Request request = new Request.Builder()
//                        .url(WebServiceURL.URL_SET_ICONID)
//                        .post(formBody)
//                        .build();
//                Call call = client.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                        e.getStackTrace();
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            Log.e("OKHTTP SET ICONID RESP", "Code: " + String.valueOf(response.code()) + " String: " + response.body().string());
//                        } else {
//                            Log.e("OKHTTP SET ICONID RESP", "Code: " + response.code());
//                        }
//                    }
//                });
            }
        });

        return imageView;
    }

}
