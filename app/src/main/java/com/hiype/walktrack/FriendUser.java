package com.hiype.walktrack;

public class FriendUser {

    private int id, iconID;
    private String username;

    public FriendUser (Integer id, Integer iconID, String username) {
        this.id = id;
        this.iconID = iconID;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public int getIconID() {
        return iconID;
    }

    public String getUsername() {
        return username;
    }
}
