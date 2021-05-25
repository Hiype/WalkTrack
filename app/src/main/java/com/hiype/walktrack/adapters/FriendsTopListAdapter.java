package com.hiype.walktrack.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hiype.walktrack.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;

public class FriendsTopListAdapter extends RecyclerView.Adapter<FriendsTopListAdapter.ViewHolder> {

    private List<JSONObject> localDataSet;
    private View view;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView friend_name;
        private final TextView friend_steps;
        private final ImageView friend_icon;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            friend_name = (TextView) view.findViewById(R.id.friend_name);
            friend_steps = (TextView) view.findViewById(R.id.friend_steps);
            friend_icon = (ImageView) view.findViewById(R.id.friend_icon);
        }

        public TextView getFriend_name() {
            return friend_name;
        }

        public TextView getFriend_steps() {
            return friend_steps;
        }

        public ImageView getFriend_icon() {
            return friend_icon;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public FriendsTopListAdapter(List<JSONObject> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.top_friends_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        String name = "icon";

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        try {
            viewHolder.getFriend_name().setText(localDataSet.get(position).getString("username"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            viewHolder.getFriend_steps().setText(localDataSet.get(position).getString("stepCount") + " " + view.getContext().getResources().getString(R.string.steps));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Class res = R.drawable.class;
        Field field;

        try {
            field = res.getField(name + localDataSet.get(position).getString("iconID"));
            int drawableId = field.getInt(null);
            viewHolder.getFriend_icon().setImageResource(drawableId);
        } catch (NoSuchFieldException | IllegalAccessException | JSONException e) {
            e.printStackTrace();
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
