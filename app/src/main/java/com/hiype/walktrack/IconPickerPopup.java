package com.hiype.walktrack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.hiype.walktrack.adapters.ImageAdapter;

public class IconPickerPopup extends DialogFragment {

    private int selected;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.e("DIALOG", "onCreateDialog called");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_icon_select, null));
        return builder.create();
    }
}
