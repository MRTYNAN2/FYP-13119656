package com.fyp.rory.fyp.Models;

/**
 * Created by Rory on 19/09/2017.
 */

public class UserFriendsID {

    private String ID;
    private String name;

    public UserFriendsID(String ID, String name){
        this.ID = ID;
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
