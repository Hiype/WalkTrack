package com.hiype.walktrack;

import android.app.Application;
import android.content.res.Resources;

import java.util.concurrent.ExecutorService;

public class GlobalVar extends Application {

    private boolean friendTopListExecuted = false;
    private boolean nightMode = false;
    private ExecutorService executorService = null;
    private String language = null;

    public Boolean getfriendTopListExecuted() {
        return friendTopListExecuted;
    }

    public Boolean getNightMode() {
        return nightMode;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public String getLanguage() {
        return language;
    }


    public void setFriendTopListExecuted(boolean friendTopListExecuted) {
        this.friendTopListExecuted = friendTopListExecuted;
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
    }

    public void setExecutorService (ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
