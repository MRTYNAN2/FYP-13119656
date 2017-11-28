package com.fyp.rory.fyp.Activitys;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private String friendProfileUrl = "";
    private int testI = 0;
    private String testUserID = "";

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private EditText searchBar;
    private Button search;

    private String likesPostCalls =  "reactions.type(LIKE).limit(0).summary(1).as(like)?,reactions.type(LOVE).limit(0).summary(1).as(love)?,reactions.type(HAHA).limit(0).summary(1).as(haha)?,reactions.type(WOW).limit(0).summary(1).as(wow)?,reactions.type(SAD).limit(0).summary(1).as(sad)?,reactions.type(ANGRY).limit(0).summary(1).as(angry)";

    private List <UserFacebookPost> wordsList = new ArrayList<>();

    private static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = (EditText) findViewById(R.id.searchbar);
        searchBar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equalsIgnoreCase("")){
                    mRecyclerAdapter.updateDataset(updates);
                } else {
                    specificSearch(searchBar.getText().toString());
                }
            }
        });

        Button speakButton = (Button) findViewById(R.id.voiceSearch);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });

        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }

        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String check =searchBar.getText().toString();
                if (!check.equalsIgnoreCase("")){
                    specificSearch(searchBar.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Please enter text",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Button postGraph = (Button)findViewById(R.id.post_button);
        postGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, User_Post_by_Graph.class);
                MainActivity.this.startActivity(mainIntent);
            }
        });

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
            friendProfileUrl = friendsList.get(testI).getProfileImage();
            testUserID = "";
            testUserID = friendsList.get(testI).getID();
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"+friendsList.get(testI).getID()+"/posts?fields=link,full_picture,source,message,is_hidden,reactions,created_time&access_token=",
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
                                                            post = new UserFacebookPost(friendName,friendProfileUrl, id.getAsString(), link.getAsString(), fullPicture.getAsString(), message.getAsString(), createdTime.getAsString(), video_source.getAsString());
                                                            myRef.child(testUserID).child(post.getmID()).setValue(post);
                                                            updates.add(post);
                                                        }
                                                        else if (fullPicture != null) {
                                                            post = new UserFacebookPost(friendName,friendProfileUrl, id.getAsString(), link.getAsString(), fullPicture.getAsString(), message.getAsString(), createdTime.getAsString(), "null");
                                                            myRef.child(testUserID).child(post.getmID()).setValue(post);
                                                            updates.add(post);
                                                        }
                                                        else {
                                                            post = new UserFacebookPost(friendName,friendProfileUrl, id.getAsString(), "noLink", "null", message.getAsString(), createdTime.getAsString(), "null");
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

    private void specificSearch(final String s) {
        ArrayList<UserFriendsID> allFriends = FriendList.getInstance().getFriendsList();
        for (int i = 0 ; i < allFriends.size(); i++) {
            DatabaseReference ref = database.getReference("users/" + allFriends.get(i).getID());
            wordsList.clear();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                        UserFacebookPost post = zoneSnapshot.getValue(UserFacebookPost.class);
                        if (post.getMessage().toLowerCase().contains(s.toLowerCase())) {
                            wordsList.add(post);
                        }
                    }
                    mRecyclerAdapter.updateDataset(wordsList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }

    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say what your looking for...");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String test = "";
            for (int i = 0 ; i < matches.size();i++){
                test += matches.get(i)+" ";
            }
            searchBar.setText(test);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
