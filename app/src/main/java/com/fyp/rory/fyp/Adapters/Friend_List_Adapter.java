package com.fyp.rory.fyp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fyp.rory.fyp.Activitys.Single_Friend_Search;
import com.fyp.rory.fyp.Models.UserFriendsID;
import com.fyp.rory.fyp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rory on 04/11/2017.
 * This is for a list for user to see all friends
 *
 */

public class Friend_List_Adapter extends RecyclerView.Adapter<Friend_List_Adapter.ViewHolderFriendList> {

    private Context fContext;
    private List<UserFriendsID> mItems;

    public Friend_List_Adapter(Context context, List<UserFriendsID> mItem){
        this.fContext = context;
        this.mItems = mItem;
    }

    @Override
    public ViewHolderFriendList onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(fContext).inflate(R.layout.friend_list_table , parent, false);

        ViewHolderFriendList vh = new ViewHolderFriendList(layoutView);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolderFriendList holder, int position) {
        final UserFriendsID friend = mItems.get(position);

        holder.oPageTitle.setText(friend.getName());

        if (!friend.getProfileImage().equalsIgnoreCase("null")) {
            Picasso.with(fContext).load(friend.getProfileImage()).fit().centerCrop().into(holder.oProfileImage);
        } else {
            holder.oProfileImage.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(fContext, Single_Friend_Search.class);
                mainIntent.putExtra("friendName",friend.getName());
                fContext.startActivity(mainIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    public void updateDataset(ArrayList<UserFriendsID> friendsList) {
        if (friendsList != null) {
            mItems = friendsList;
            notifyDataSetChanged();
            Log.d("FBFriend", "new data set size = " + friendsList.size());
        }
    }

    public static class ViewHolderFriendList extends RecyclerView.ViewHolder {
        public TextView oPageTitle;
        public ImageView oProfileImage;
        public LinearLayout layout;

        public ViewHolderFriendList(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.fbItemWrapper);
            oProfileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            oPageTitle = (TextView) itemView.findViewById(R.id.fbFriendName);
        }
    }
}
