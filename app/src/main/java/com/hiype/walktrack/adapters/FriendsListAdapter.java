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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.FriendUser;
import com.hiype.walktrack.MySqlApi;
import com.hiype.walktrack.R;
import com.hiype.walktrack.http.RequestHandler;
import com.hiype.walktrack.http.WebServiceURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private List<FriendUser> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView friend_name;
        private final ImageView friend_icon;
        private final Button unfriend_btn;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            friend_name = (TextView) view.findViewById(R.id.friends_name);
            friend_icon = (ImageView) view.findViewById(R.id.friends_icon);
            unfriend_btn = (Button) view.findViewById(R.id.remove_friend_button);
        }

        public TextView getFriend_name() {
            return friend_name;
        }

        public ImageView getFriend_icon() {
            return friend_icon;
        }

        public Button getUnfriend_btn() {
            return unfriend_btn;
        }
    }

    public FriendsListAdapter(List<FriendUser> dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_list_row_item, parent, false);

        return new FriendsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String name = "icon";

        int id = localDataSet.get(position).getId();

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

        holder.getUnfriend_btn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper db = new DBHelper(v.getContext());

                removeFriend(holder.getAdapterPosition(), id, v.getContext());
                db.removeFriend(id);
                db.close();
            }
        });

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void clearItem(int position) {
        Log.e("LOCALDATASET", "Data: " + localDataSet.toString() + ", Position received: " + position);
        localDataSet.remove(position);
        Log.e("LOCALDATASET", "Data after removal : " + localDataSet.toString() + ", Position received: " + position);
        this.notifyItemRemoved(position);
    }

    private void removeFriend(int position ,int id, Context context) {

        JSONObject obj = MySqlApi.removeFriend(id, context);

        try {
            if (!obj.getBoolean("error")) {
                clearItem(position);
            } else {
                Log.e("Friends list", "No friend was found!");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
