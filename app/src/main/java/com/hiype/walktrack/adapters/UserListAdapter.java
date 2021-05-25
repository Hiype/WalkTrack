package com.hiype.walktrack.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.FriendUser;
import com.hiype.walktrack.R;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

private ArrayList<FriendUser> localDataSet;

/**
 * Provide a reference to the type of views that you are using
 * (custom ViewHolder).
 */
public static class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView friend_name;
    private final ImageView friend_icon;
    private final Button add_user_button;

    public ViewHolder(View view) {
        super(view);
        // Define click listener for the ViewHolder's View

        friend_name = (TextView) view.findViewById(R.id.friends_name);
        friend_icon = (ImageView) view.findViewById(R.id.friends_icon);
        add_user_button = (Button) view.findViewById(R.id.add_user_button);
    }

    public TextView getFriend_name() {
        return friend_name;
    }

    public ImageView getFriend_icon() {
        return friend_icon;
    }

    public Button getAdd_user_button() {
        return add_user_button;
    }
}

    public UserListAdapter(ArrayList<FriendUser> dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public UserListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row_item, parent, false);

        return new UserListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.ViewHolder holder, int position) {
        String name = "icon";

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        holder.getFriend_name().setText(localDataSet.get(position).getUsername());

        Class res = R.drawable.class;
        Field field;

        try {
            field = res.getField(name + localDataSet.get(position).getIconID());
            int drawableId = field.getInt(null);
            holder.getFriend_icon().setImageResource(drawableId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        holder.getAdd_user_button().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DBHelper(v.getContext()).addFriend(localDataSet.get(position).getId());
                addFriend(position, v.getContext());
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void clear() {
        int size = localDataSet.size();
        if(size > 0) {
            for ( int i = 0; i < size; i++) {
                localDataSet.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void clearItem(int position) {
    localDataSet.remove(position);
    this.notifyItemRemoved(position);
    }

    private void addFriend(int position, Context context) {

        DBHelper db = new DBHelper(context);

        class FriendAdder extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
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


                try {
                    JSONObject obj = new JSONObject(s);

                    ArrayList<FriendUser> searchResultArray = new ArrayList<>();
                    try {
                        if (!obj.getBoolean("error")) {
                            clearItem(position);
                        } else {
                            Log.e("Friends list", "No friends were found!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
                params.put("user_id", String.valueOf(db.getCurrentUserID()));
                params.put("added_friend", String.valueOf(localDataSet.get(position).getId()));

                //returning the Response of user login
                return requestHandler.sendPostRequest(WebServiceURL.URL_ADD_FRIEND, params);
            }
        }
        FriendAdder fa = new FriendAdder();
        fa.execute();
    }
}
