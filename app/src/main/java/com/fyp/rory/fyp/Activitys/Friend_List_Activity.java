package com.fyp.rory.fyp.Activitys;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fyp.rory.fyp.Adapters.Friend_List_Adapter;
import com.fyp.rory.fyp.Models.FriendList;
import com.fyp.rory.fyp.R;

public class Friend_List_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.friendList_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        Friend_List_Adapter mRecyclerAdapter = new Friend_List_Adapter(this, FriendList.getInstance().getFriendsList());

        mRecyclerView.setAdapter(mRecyclerAdapter);

    }
}
