package com.hiype.walktrack;

import android.util.Log;

public class User {

    private String username, email, height, date_joined, friends_ids, claimed_icons, lang;
    private int id, stepCount, iconID, totalSteps,  has_desktop, nightMode, points;

    public User(Integer id, String height, String username, String email, String date_joined, Integer stepCount, String friends_ids, Integer iconID, Integer has_desktop, Integer totalSteps, Integer nightMode, Integer points, String claimed_icons, String lang) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.height = height;
        this.date_joined = date_joined;
        this.stepCount = stepCount;
        this.friends_ids = friends_ids;
        Log.e("FRIENDS IDS USER CLASS CONSTR", "Friends ids: " + this.friends_ids);
        this.iconID = iconID;
        this.has_desktop = has_desktop;
        this.totalSteps = totalSteps;
        this.nightMode = nightMode;
        this.points = points;
        this.claimed_icons = claimed_icons;
        this.lang = lang;
    }

    //Set methods
    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    //Get methods
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getHeight() {
        return height;
    }

    public String getDate_joined() {
        return date_joined;
    }

    public int getStepCount() {
        return stepCount;
    }

    public String getFriends_ids() {
        Log.e("FRIENDS IDS USER CLASS", "Friends ids: " + friends_ids);
        return friends_ids;
    }

    public int getIconID() {
        return iconID;
    }

    public int getHasDesktop() {
        return has_desktop;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public int getPoints() {
        return points;
    }

    public int getNightMode() {
        return nightMode;
    }

    public String getClaimed_icons() {
        return claimed_icons;
    }

    public String getLanguage() {
        return lang;
    }
}
