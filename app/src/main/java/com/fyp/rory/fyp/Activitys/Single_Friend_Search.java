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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

/*
 THIS ClASS is for performing searching on a single friends post
 Basically a practice run.
 */

public class Single_Friend_Search extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private List<UserFacebookPost> updates = new ArrayList<>();;
    private UserFacebookAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private EditText searchBar;
    private Button search;

    private UserFriendsID friend;

    private static final int REQUEST_CODE = 1234;
    private List <UserFacebookPost> wordsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single__friend__search);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

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

        Button speakButton = (Button) findViewById(R.id.voice_button);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });

        wordsList = new ArrayList<>();

        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String check =searchBar.getText().toString();
                if (!check.equalsIgnoreCase("")){
                    specificSearch(searchBar.getText().toString());
                } else {
                    Toast.makeText(Single_Friend_Search.this, "Please enter text",
                            Toast.LENGTH_LONG).show();
                }
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

        Intent intent = getIntent();
        String name = intent.getStringExtra("friendName");
        friend = FriendList.getInstance().getSingleFriend(name);
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDatabase.getReference("users/"+friend.getID());
        ref.limitToLast(15);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot zoneSnapshot: dataSnapshot.getChildren()) {
                    UserFacebookPost post = zoneSnapshot.getValue(UserFacebookPost.class);
                    updates.add(post);
                    mRecyclerAdapter.updateDataset(updates);
                    System.out.println(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.facebook_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        mRecyclerAdapter = new UserFacebookAdapter(this);

        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private void specificSearch(final String s) {
        DatabaseReference ref = mDatabase.getReference("users/"+friend.getID());
        wordsList.clear();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot zoneSnapshot: dataSnapshot.getChildren()) {
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
