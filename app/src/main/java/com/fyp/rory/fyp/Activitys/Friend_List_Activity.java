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

import com.fyp.rory.fyp.Adapters.Friend_List_Adapter;
import com.fyp.rory.fyp.Models.FriendList;
import com.fyp.rory.fyp.Models.UserFriendsID;
import com.fyp.rory.fyp.R;

import java.util.ArrayList;
import java.util.List;

public class Friend_List_Activity extends AppCompatActivity {

    private Friend_List_Adapter mRecyclerAdapter;
    private EditText searchBar;
    private Button search;
    private static final int REQUEST_CODE = 1234;

    private ArrayList<UserFriendsID> searchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

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
                    mRecyclerAdapter.updateDataset(FriendList.getInstance().getFriendsList());
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

        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.friendList_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        mRecyclerAdapter = new Friend_List_Adapter(this, FriendList.getInstance().getFriendsList());

        mRecyclerView.setAdapter(mRecyclerAdapter);

        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String check =searchBar.getText().toString();
                if (!check.equalsIgnoreCase("")){
                    specificSearch(searchBar.getText().toString());
                } else {
                    Toast.makeText(Friend_List_Activity.this, "Please enter text",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void specificSearch(String s) {
        if (searchList != null) {
            searchList.clear();
        }
        ArrayList<UserFriendsID> allFriends =  FriendList.getInstance().getFriendsList();
        for (int i = 0 ; i < allFriends.size();i++){
                if (allFriends.get(i).getName().toLowerCase().contains(s.toLowerCase())){
                    searchList.add(allFriends.get(i));
                }
            }
        mRecyclerAdapter.updateDataset(searchList);
    }

    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your friends name.");
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
