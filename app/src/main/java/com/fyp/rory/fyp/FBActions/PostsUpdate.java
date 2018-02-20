package com.fyp.rory.fyp.FBActions;

import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fyp.rory.fyp.Activitys.MainActivity;
import com.fyp.rory.fyp.Models.FBReactions;
import com.fyp.rory.fyp.Models.FriendList;
import com.fyp.rory.fyp.Models.UserFacebookPost;
import com.fyp.rory.fyp.Models.UserFriendsID;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rory on 20/02/2018.
 * Class to update friends facebook post from last time/date recorded
 */

public class PostsUpdate {
    private int testI = 0;
    private List<UserFriendsID> friendsList = new ArrayList<>();
    private String friendName;
    private String friendProfileUrl;
    private String testUserID;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String before;

    public void getPosts(){
        database = FirebaseDatabase.getInstance();
        friendsList = FriendList.getInstance().getFriendsList();
        myRef = database.getReference("users");
            if (testI < friendsList.size()) {
                friendName = friendsList.get(testI).getName();
                friendProfileUrl = friendsList.get(testI).getProfileImage();
                testUserID = "";
                testUserID = friendsList.get(testI).getID();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query lastQuery = databaseReference.child("users/" + friendsList.get(testI).getID()).orderByKey().limitToLast(1);
                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                            UserFacebookPost post = zoneSnapshot.getValue(UserFacebookPost.class);
                            before = post.getTimeCreated();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Handle possible errors.
                    }
                });
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/"+friendsList.get(testI).getID()+"/posts?fields=link,full_picture,source,message,is_hidden,reactions,created_time&since="+before+"&&access_token=",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
            /* handle the result */
                                if (response != null) {
                                    try {
                                        String responseStr = response.getJSONObject().toString();
                                        Log.d("FaceBook Post", responseStr);
//                            responseStr = responseStr.replace("{Response:  \n" +
//                                    "responseCode: 200, \n" +
//                                    "graphObject:","");
//                            responseStr = responseStr.replace(", error: null}","");
                                        if (response != null && !responseStr.isEmpty()) {
                                            JsonParser parser = new JsonParser();
                                            JsonElement element = parser.parse(responseStr);
                                            if (element != null && element.isJsonObject() && !element.isJsonNull()) {
                                                JsonObject data = element.getAsJsonObject();
                                                JsonElement jsonArray = data.get("data");

                                                if (jsonArray.isJsonArray() && !jsonArray.isJsonNull()) {
                                                    JsonArray array = jsonArray.getAsJsonArray();
//                                        List<FacebookPost> updates = new ArrayList<>();
                                                    // if its to refresh the list we clear it
                                                    for (JsonElement object : array) {
                                                        if (object != null && object.isJsonObject() && !object.isJsonNull()) {
                                                            FBReactions reactionsItems = new FBReactions();
                                                            JsonObject obj = object.getAsJsonObject();
                                                            JsonElement id = obj.get("id");
                                                            JsonElement link;
                                                            if (obj.has("reactions")) {
                                                                JsonObject reactionObj = obj.getAsJsonObject("reactions");
                                                                if (reactionObj.has("data")) {
                                                                    JsonArray reactData = reactionObj.getAsJsonArray("data");
                                                                    for (int i = 0; i < reactData.size(); i++) {
                                                                        JsonObject tempObj = reactData.get(i).getAsJsonObject();
                                                                        if (tempObj.get("type").getAsString().equalsIgnoreCase("WOW")) {
                                                                            reactionsItems.setFbWOW(true);
                                                                        } else if (tempObj.get("type").getAsString().equalsIgnoreCase("HAHA")) {
                                                                            reactionsItems.setFbHAHA(true);
                                                                        } else if (tempObj.get("type").getAsString().equalsIgnoreCase("LOVE")) {
                                                                            reactionsItems.setFbLOVE(true);
                                                                        } else if (tempObj.get("type").getAsString().equalsIgnoreCase("LIKE")) {
                                                                            reactionsItems.setFbLIKE(true);
                                                                        } else if (tempObj.get("type").getAsString().equalsIgnoreCase("SAD")) {
                                                                            reactionsItems.setFbSAD(true);
                                                                        } else if (tempObj.get("type").getAsString().equalsIgnoreCase("ANGERY")) {
                                                                            reactionsItems.setFbANGERY(true);
                                                                        }
                                                                    }
                                                                    Log.d("FBREACT", "Reations found");
                                                                }
                                                            }
                                                            if (obj.has("link")) {
                                                                link = obj.get("link");
                                                            } else {
                                                                link = null;
                                                            }
                                                            JsonElement fullPicture;
                                                            if (obj.has("full_picture")) {
                                                                fullPicture = obj.get("full_picture");
                                                            } else {
                                                                fullPicture = null;
                                                            }
                                                            JsonElement message = obj.get("message");
                                                            JsonElement createdTime = obj.get("created_time");
                                                            JsonElement video_source;
                                                            // source returns a raw url of a video in a post. If a post does not have a video nothing is returns for source
                                                            if (obj.has("source")) {
                                                                video_source = obj.get("source");
                                                                Log.d("FBook Src:", video_source.getAsString() + " - " + link.getAsString());
                                                            } else {
                                                                video_source = null;
                                                            }

                                                            UserFacebookPost post;
                                                            if (message != null && !message.isJsonNull() && message.getAsString() != null && !message.getAsString().isEmpty()
                                                                    && createdTime != null && !createdTime.isJsonNull() && createdTime.getAsString() != null && !createdTime.getAsString().isEmpty()) {
                                                                if (fullPicture != null && video_source != null && link != null) {
                                                                    post = new UserFacebookPost(friendName, friendProfileUrl, id.getAsString(), link.getAsString(), fullPicture.getAsString(), message.getAsString(), createdTime.getAsString(), video_source.getAsString(), reactionsItems);
                                                                    myRef.child(testUserID).child(post.getmID()).setValue(post);
                                                                } else if (fullPicture != null) {
                                                                    post = new UserFacebookPost(friendName, friendProfileUrl, id.getAsString(), link.getAsString(), fullPicture.getAsString(), message.getAsString(), createdTime.getAsString(), "null", reactionsItems);
                                                                    myRef.child(testUserID).child(post.getmID()).setValue(post);
                                                                } else {
                                                                    post = new UserFacebookPost(friendName, friendProfileUrl, id.getAsString(), "noLink", "null", message.getAsString(), createdTime.getAsString(), "null", reactionsItems);
                                                                    myRef.child(testUserID).child(post.getmID()).setValue(post);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    //MainActivity.loadPosts();
                                                }
                                            }
                                        }
                                    } catch (NullPointerException e){
                                     // this is just to catch null exception when no new posts are found
                                    }
                                }
                                testI++;
                                getPosts();
                            }
                        }
                ).executeAsync();
        } else {
                MainActivity.notifyPostsFinished();
            }

    }
}
