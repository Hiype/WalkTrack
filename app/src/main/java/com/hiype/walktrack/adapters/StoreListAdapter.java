package com.hiype.walktrack.adapters;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.hiype.walktrack.DBHelper;
import com.hiype.walktrack.MySqlApi;
import com.hiype.walktrack.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.ViewHolder> {

    ViewGroup viewGroup;

    private List<List<String>> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView list_name, list_points;
        private final GifImageView list_icon;
        private final Button buy_icon_btn;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            list_name = (TextView) view.findViewById(R.id.store_list_name);
            list_points = (TextView) view.findViewById(R.id.store_list_points);
            list_icon = (GifImageView) view.findViewById(R.id.store_list_icon);
            buy_icon_btn = (Button) view.findViewById(R.id.buy_icon_button);
        }

        public TextView getList_name() {
            return list_name;
        }

        public TextView getList_points() {
            return list_points;
        }

        public GifImageView getList_icon() {
            return list_icon;
        }

        public Button getBuy_icon_btn() {
            return buy_icon_btn;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public StoreListAdapter(List<List<String>> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StoreListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.store_list_row_item, viewGroup, false);

        this.viewGroup = viewGroup;

        return new StoreListAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(StoreListAdapter.ViewHolder viewHolder, final int position) {
        Context context = viewGroup.getContext();
        DBHelper db = new DBHelper(context);

        String name = "icon";
        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.getList_name().setText(localDataSet.get(position).get(0));

        String points_txt = localDataSet.get(position).get(1) + " Points";

        viewHolder.getList_points().setText(points_txt);

        Class res = R.drawable.class;
        Field field;

        try {
            field = res.getField(name + localDataSet.get(position).get(2));
            int drawableId = field.getInt(null);
            viewHolder.getList_icon().setImageResource(drawableId);
            Log.e("STORELISTADAPTER", "Is not a gif: " + localDataSet.get(position).get(2));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        viewHolder.getBuy_icon_btn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int points = db.getPoints();
                int price = Integer.parseInt(localDataSet.get(position).get(1));

                if(points >= price) {
                    Log.e("ICONBUY", "User has enough points to buy icon for: " + price);
                    int sum = points - price;
                    db.updatePoints(sum);
                    db.addClaimedIcon(Integer.parseInt(localDataSet.get(position).get(2)), viewGroup.getContext());
                    MySqlApi.addClaimedIcon(Integer.parseInt(localDataSet.get(position).get(2)), viewGroup.getContext());
                } else {
                    Log.e("ICONBUY", "User does not have enough points to buy icon for: " + price);
                    Toast.makeText(context, "Insufficient points", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}
