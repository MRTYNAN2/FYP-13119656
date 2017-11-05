package com.fyp.rory.fyp.Models;

import java.util.ArrayList;

/**
 * Created by Rory on 04/11/2017.
 */

public class FriendList {
    private static FriendList ourInstance;

    private ArrayList <UserFriendsID> myFriend;

    public static FriendList getInstance() {
        if (ourInstance == null){
            ourInstance = new FriendList();
        }
        return ourInstance;
    }

    private FriendList() {
        myFriend = new ArrayList<>();
    }

    public void addUser(UserFriendsID friend){
        myFriend.add(friend);
    }

    public ArrayList<UserFriendsID> getFriendsList(){
        return myFriend;
    }


}
