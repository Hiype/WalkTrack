package com.hiype.walktrack.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hiype.walktrack.R;

import java.util.List;

public class PCListAdapter extends RecyclerView.Adapter<PCListAdapter.ViewHolder> {

    private List<String> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView pc_name;
        private final ImageView pc_country;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            pc_name = (TextView) view.findViewById(R.id.pc_name);
            pc_country = (ImageView) view.findViewById(R.id.pc_country_icon);
        }

        public TextView getPc_name() {
            return pc_name;
        }

        public ImageView getPc_country_icon() {
            return pc_country;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public PCListAdapter(List<String> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PCListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.pc_list_row_item, viewGroup, false);

        return new PCListAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)

    @Override
    public void onBindViewHolder(PCListAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.getPc_name().setText(localDataSet.get(position));


        viewHolder.getPc_country_icon().setImageResource(R.drawable.icon1);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
