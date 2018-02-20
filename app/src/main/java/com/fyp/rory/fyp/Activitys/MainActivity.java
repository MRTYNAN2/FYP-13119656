package com.fyp.rory.fyp.Activitys;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.fyp.rory.fyp.Adapters.UserFacebookAdapter;
import com.fyp.rory.fyp.FBActions.FBGetFriends;
import com.fyp.rory.fyp.FBActions.GetPostsInit;
import com.fyp.rory.fyp.FBActions.PostsUpdate;
import com.fyp.rory.fyp.Models.FBReactions;
import com.fyp.rory.fyp.Models.FriendList;
import com.fyp.rory.fyp.Models.UserFacebookPost;
import com.fyp.rory.fyp.Models.UserFriendsID;
import com.fyp.rory.fyp.R;
import com.fyp.rory.fyp.Utilitys.PostsSort;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static List<UserFacebookPost> updates = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    private static UserFacebookAdapter mRecyclerAdapter;
    private static FirebaseDatabase database;
    private EditText searchBar;
    private static ProgressBar prog;

    // private String likesPostCalls =  "reactions.type(LIKE).limit(0).summary(1).as(like)?,reactions.type(LOVE).limit(0).summary(1).as(love)?,reactions.type(HAHA).limit(0).summary(1).as(haha)?,reactions.type(WOW).limit(0).summary(1).as(wow)?,reactions.type(SAD).limit(0).summary(1).as(sad)?,reactions.type(ANGRY).limit(0).summary(1).as(angry)";
    private List <UserFacebookPost> wordsList = new ArrayList<>();
    private static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prog = findViewById(R.id.progbar);
        searchBar = findViewById(R.id.searchbar);
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
                }
//                else {
//                    //specificSearch(searchBar.getText().toString());
//                }
            }
        });

        Button speakButton = findViewById(R.id.voiceSearch);
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
            speakButton.setText(R.string.speach_text);
        }

        Button search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String check =searchBar.getText().toString();
//                if (check.split("\\s+").length == 0){
                    if (check.toLowerCase().equalsIgnoreCase("like") || check.toLowerCase().equalsIgnoreCase("likes")){
                        reactionSearch("like");
                    }
                    else if (check.toLowerCase().equalsIgnoreCase("haha") || check.toLowerCase().equalsIgnoreCase("funny")){
                    reactionSearch("haha");
                    }
                    else if (check.toLowerCase().equalsIgnoreCase("angery") || check.toLowerCase().equalsIgnoreCase("mad")){
                    reactionSearch("angery");
                    }
                    else if (check.toLowerCase().equalsIgnoreCase("sad") || check.toLowerCase().equalsIgnoreCase("cry")){
                        reactionSearch("sad");
                    }
                    else if (check.toLowerCase().equalsIgnoreCase("love") || check.toLowerCase().equalsIgnoreCase("<3")){
                        reactionSearch("love");
                    }
                    else if (check.toLowerCase().equalsIgnoreCase("wow") || check.toLowerCase().equalsIgnoreCase("omg")){
                        reactionSearch("wow");
                    }
                else if (!check.equalsIgnoreCase("")){
                    specificSearch(searchBar.getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "Please enter text",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("users");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Button postGraph = findViewById(R.id.post_button);
        postGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, User_Post_by_Graph.class);
                MainActivity.this.startActivity(mainIntent);
            }
        });

        Button friends = findViewById(R.id.friendsList);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(MainActivity.this, Friend_List_Activity.class);
                MainActivity.this.startActivity(mainIntent);
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.facebook_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        mRecyclerAdapter = new UserFacebookAdapter(this);

        mRecyclerView.setAdapter(mRecyclerAdapter);

        getFriends();

        Button logout = findViewById(R.id.logout_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                //Intent mainIntent = new Intent(MainActivity.this, Login_Activity.class);
                //MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            }
        });

    }

    public static void notifyFriendsListFinished(){
        getFriendsPost();
    }

    public static void notifyPostsFinished(){
        loadPosts();
    }

    public static void loadPosts(){
        ArrayList<UserFriendsID> allFriends = FriendList.getInstance().getFriendsList();
        for (int i = 0 ; i < allFriends.size(); i++) {
            updates.clear();
            DatabaseReference ref = database.getReference("users/" + allFriends.get(i).getID());
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                        UserFacebookPost post = zoneSnapshot.getValue(UserFacebookPost.class);
                        updates.add(post);
                        Collections.sort(updates,new PostsSort());
                    }
                    Collections.sort(updates,new PostsSort());
                    mRecyclerAdapter.updateDataset(updates);
                    prog.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }

    public static void getFriendsPost(){
        PostsUpdate friendsPost = new PostsUpdate();
        friendsPost.getPosts();
    }

    public void getFriends(){
        FBGetFriends yourFriends = new FBGetFriends();
        yourFriends.getAllFriends(MainActivity.this);
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
                        // This is the format that we receive the date_created field
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat facebookTimeCreatedFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                        //This is the format facebook displays dates on site "/" this is to be replaced with "at"
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDate = new SimpleDateFormat("d MMMM yyyy / HH:mm");
                        Date stringDate = null;
                        try {
                            stringDate = facebookTimeCreatedFormat.parse(post.getTimeCreated());

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String finalDate = outputDate.format(stringDate);
                        if (post.getMessage().toLowerCase().contains(s.toLowerCase())) {
                            wordsList.add(post);
                        } else if(finalDate.toLowerCase().contains(s.toLowerCase())){
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

    private void reactionSearch(final String s) {
        ArrayList<UserFriendsID> allFriends = FriendList.getInstance().getFriendsList();
        for (int i = 0 ; i < allFriends.size(); i++) {
            DatabaseReference ref = database.getReference("users/" + allFriends.get(i).getID());
            wordsList.clear();
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
                        UserFacebookPost post = zoneSnapshot.getValue(UserFacebookPost.class);
                        FBReactions postReactions = post.getmReactions();
                        switch (s) {
                            case "like":
                                if (postReactions.isFbLIKE()){
                                    wordsList.add(post);
                                }
                                break;
                            case "haha":
                                if (postReactions.isFbHAHA()){
                                    wordsList.add(post);
                                }
                                break;
                            case "angry":
                                if (postReactions.isFbANGERY()){
                                    wordsList.add(post);
                                }
                                break;
                            case "sad":
                                if (postReactions.isFbSAD()){
                                    wordsList.add(post);
                                }
                                break;
                            case "love":
                                if (postReactions.isFbLOVE()){
                                    wordsList.add(post);
                                }
                                break;
                            case "wow":
                                if (postReactions.isFbLOVE()){
                                    wordsList.add(post);
                                }
                                break;
                            default:
                                break;
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
            StringBuilder test = new StringBuilder();
            for (int i = 0 ; i < matches.size();i++){
                test.append(matches.get(i)).append(" ");
            }
            searchBar.setText(test.toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void notifyNewUser() {
        GetPostsInit initPosts = new GetPostsInit();
        initPosts.getPosts();
    }
}
