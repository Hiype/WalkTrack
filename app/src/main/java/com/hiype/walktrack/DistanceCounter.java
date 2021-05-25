package com.hiype.walktrack;

import android.util.Log;

public class DistanceCounter {

    public static String calculateDistance(int height, int stepsWalked) {
        double singleStep;
        double distance;

        //Calculate single step distance
        singleStep = height * 0.414;
        Log.e("DISTANCE FUNC", "Single step: " + singleStep);

        //Calculate distance in cm
        distance = singleStep * stepsWalked;
        Log.e("DISTANCE FUNC", "Distance in cm: " + distance);

        //Calculate distance in km without rounding
        distance = distance / 100000;
        Log.e("DISTANCE FUNC", "Distance in km (before rounding): " + distance);

        //Calculate distance in km rounded final
        Log.e("DISTANCE FUNC", "Distance in km final: " + String.format("%.2f", distance));

        return String.format("%.2f", distance);
    }
}
