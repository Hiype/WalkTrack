package com.hiype.walktrack;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.hiype.walktrack.adapters.ImageAdapter;

public class DialogIconSelect extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_icon_select);
        Log.e("DIALOG", "view created");

        GridView gridview = (GridView) findViewById(R.id.icon_select_grid);
        gridview.setAdapter(new ImageAdapter(this));
    }
}
