package com.fyp.rory.fyp.Models;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Rory on 19/09/2017.
 * Default users friends object
 */

public class UserFriendsID {

    private String ID;
    private String name;
    private String profileImage;

    public UserFriendsID(String ID, String name){
        this.ID = ID;
        this.name = name;
        getProfilePic();
    }

    private void getProfilePic() {

        try {
            this.profileImage = new URL("https://graph.facebook.com/"+ID+"/picture?type=small").toString();
            Log.d("UserPic",profileImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
