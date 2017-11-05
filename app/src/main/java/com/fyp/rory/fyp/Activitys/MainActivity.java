package com.fyp.rory.fyp.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.fyp.rory.fyp.Adapters.UserFacebookAdapter;
import com.fyp.rory.fyp.Models.FriendList;
import com.fyp.rory.fyp.Models.UserFacebookPost;
import com.fyp.rory.fyp.Models.UserFriendsID;
import com.fyp.rory.fyp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button logout;

    private List<UserFacebookPost> updates = new ArrayList<>();
    private List<UserFriendsID> friendsList = new ArrayList<>();
    private UserFacebookAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;

    private String friendName = "";
    private int testI = 0;
    private String testUserID = "";

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Button friends = (Button)findViewById(R.id.friendsList);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, Friend_List_Activity.class);
                MainActivity.this.startActivity(mainIntent);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.facebook_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        mRecyclerAdapter = new UserFacebookAdapter(this);

        mRecyclerView.setAdapter(mRecyclerAdapter);

//        new GraphRequest(
//                AccessToken.getCurrentAccessToken(),"/me/friends ",null,HttpMethod.GET,new GraphRequest.Callback(){
//            @Override
//            public void onCompleted(GraphResponse response) {
//                if (response != null) {
//                    String responseStr = response.getJSONObject().toString();
//                }
//            }
//        }
//        ).executeAsync();

        getFriends();

        logout = (Button)findViewById(R.id.logout_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent mainIntent = new Intent(MainActivity.this, Login_Activity.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            }
        });

    }

    public void getFriendsPost(){
        friendsList = FriendList.getInstance().getFriendsList();
        if (testI < friendsList.size()) {
            friendName = friendsList.get(testI).getName();
            testUserID = "";
            testUserID = friendsList.get(testI).getID();
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"+friendsList.get(testI).getID()+"/posts?fields=link,full_picture,source,message,is_hidden,created_time&access_token=",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
            /* handle the result */
                            if (response != null) {
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
                                                    JsonObject obj = object.getAsJsonObject();
                                                    JsonElement id = obj.get("id");
                                                    JsonElement link;
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
                                                    JsonElement video_source = null;
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
                                                            post = new UserFacebookPost(friendName, id.getAsString(), link.getAsString(), fullPicture.getAsString(), message.getAsString(), createdTime.getAsString(), video_source.getAsString());
                                                            myRef.child(testUserID).child(post.getmID()).setValue(post);
                                                            updates.add(post);
                                                        }
                                                        else if (video_source == null && fullPicture != null && link != null) {
                                                            post = new UserFacebookPost(friendName, id.getAsString(), link.getAsString(), fullPicture.getAsString(), message.getAsString(), createdTime.getAsString(), "null");
                                                            myRef.child(testUserID).child(post.getmID()).setValue(post);
                                                            updates.add(post);
                                                        }
                                                        else {
                                                            post = new UserFacebookPost(friendName, id.getAsString(), "noLink", "null", message.getAsString(), createdTime.getAsString(), "null");
                                                            myRef.child(testUserID).child(post.getmID()).setValue(post);
                                                            updates.add(post);
                                                          }
                                                        }
                                                }
                                            }
                                            mRecyclerAdapter.updateDataset(updates);
                                        }
                                    }
                                }
                            }
                            testI++;
                            getFriendsPost();
                        }
                    }
            ).executeAsync();
        }
    }

    public void getFriends (){
                new GraphRequest(
                AccessToken.getCurrentAccessToken(),"/me/friends?",null,HttpMethod.GET,new GraphRequest.Callback(){
            @Override
            public void onCompleted(GraphResponse response) {
                if (response != null) {
                    String responseStr = response.getJSONObject().toString();
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(responseStr);
                    if (element != null && element.isJsonObject() && !element.isJsonNull()) {
                        JsonObject data = element.getAsJsonObject();
                        JsonElement jsonArray = data.get("data");
                        if (jsonArray.isJsonArray() && !jsonArray.isJsonNull()) {
                            JsonArray array = jsonArray.getAsJsonArray();
                            for (JsonElement object : array) {
                                if (object != null && object.isJsonObject() && !object.isJsonNull()) {
                                    JsonObject obj = object.getAsJsonObject();
                                    JsonElement id = obj.get("id");
                                    JsonElement name = obj.get("name");


                                    UserFriendsID friend = new UserFriendsID(id.getAsString(),name.getAsString());
                                    FriendList.getInstance().addUser(friend);
                                    //friendsList.add(friend);
                                }
                            }
                        }
                        getFriendsPost();
                    }

                }
            }
        }
        ).executeAsync();
    }
}
