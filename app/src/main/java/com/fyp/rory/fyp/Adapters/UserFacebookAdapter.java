package com.fyp.rory.fyp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fyp.rory.fyp.Models.UserFacebookPost;
import com.fyp.rory.fyp.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserFacebookAdapter extends RecyclerView.Adapter<UserFacebookAdapter.ViewHolder> {
    private static final String DEBUG_TAG = "FbAdapt";
    private Context mContext;
    private List<UserFacebookPost> mItems;

    public UserFacebookAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void updateDataset(List<UserFacebookPost> data) {
        if (data != null) {
            mItems = data;
            notifyDataSetChanged();
            Log.d(DEBUG_TAG, "new data set size = " + data.size());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(mContext).inflate(R.layout.user_facebook_item_layout, parent, false);

        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserFacebookPost post = mItems.get(position);
        //this gets Picasso to handle grabbing the profile picture of the facebook account
        //if (!post.getmProfileImage().equalsIgnoreCase("null")){
            Picasso.with(mContext).load(post.getmProfileImage()).fit().centerCrop().into(holder.oProfileImage);
//        }else {
//            holder.oProfileImage.setVisibility(View.GONE);
//        }
        // there is no call for user name hardcoded above

        holder.oPageTitle.setText(post.getmName());

        //facebook cuts out long text at 9 lines so In xml I have set to 8
        //text will be clickable to expand to show full text
        holder.oMessageTextView.setText(post.getMessage());
        holder.oMessageTextView.setMaxLines(8);

        holder.oMessageTextView.setEllipsize(holder.oMessageTextView.getEllipsize());
        //simple on click listener if text is expandable it will expand (I removed closing again as facebook do not do it)
        holder.oMessageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.oMessageTextView.getMaxLines() <= 8) {
                    holder.oMessageTextView.setMaxLines(Integer.MAX_VALUE);
                }
            }
        });

        // if there is video source display play button. soucre is a raw link to video (In case need or implement video in future)
        if (!post.getmVideoSoucre().equalsIgnoreCase("")) {
            holder.oPlayIcon.setVisibility(View.VISIBLE);
        } else {
            holder.oPlayIcon.setVisibility(View.GONE);

        }
        // post without images are ignored but just in case. again simple on click on image to go to link.
        if (!post.getPicture().equalsIgnoreCase("null")&& !post.getPicture().isEmpty()) {
            holder.imageArea.setVisibility(View.VISIBLE);
            holder.oImageView.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(post.getPicture()).fit().centerCrop().into(holder.oImageView);
            holder.oImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!post.getLink().isEmpty()) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(post.getLink()));
                        mContext.startActivity(i);
                    }
                }
            });
        } else {
            holder.imageArea.setVisibility(View.GONE);
            holder.oImageView.setVisibility(View.GONE);
        }

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
        finalDate = finalDate.replace("/", "at");
        if (stringDate != null)
            holder.oDateTextView.setText(finalDate);

      //  this is the top of the screen (profile pic, Name , date) same click on go to link
        holder.oWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post.getLink().equalsIgnoreCase("")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(post.getLink()));
                    mContext.startActivity(i);
                }
            }
        });

        if(!post.getmReactions().isFbLIKE()){
            holder.likes.setVisibility(View.GONE);
        } else {
            holder.likes.setVisibility(View.VISIBLE);
        }
        if(!post.getmReactions().isFbLOVE()){
            holder.loves.setVisibility(View.GONE);
        } else {
            holder.loves.setVisibility(View.VISIBLE);
        }
        if(!post.getmReactions().isFbHAHA()){
            holder.haha.setVisibility(View.GONE);
        } else {
            holder.haha.setVisibility(View.VISIBLE);
        }
        if(!post.getmReactions().isFbWOW()){
            holder.wows.setVisibility(View.GONE);
        } else {
            holder.wows.setVisibility(View.VISIBLE);
        }
        if(!post.getmReactions().isFbSAD()){
            holder.sad.setVisibility(View.GONE);
        } else {
            holder.sad.setVisibility(View.VISIBLE);
        }
        if(!post.getmReactions().isFbANGERY()){
            holder.angry.setVisibility(View.GONE);
        } else {
            holder.angry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mItems == null) {
            return 0;
        }
        return mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View oWrapper;
        TextView oMessageTextView;
        TextView oPageTitle;
        ImageView oImageView;
        TextView oDateTextView;
        ImageView oProfileImage;
        ImageView oPlayIcon, likes, loves, wows, sad, angry, haha;
        RelativeLayout imageArea;

        ViewHolder(View itemView) {
            super(itemView);

            oWrapper = itemView.findViewById(R.id.fbItemWrapper);
            oMessageTextView = itemView.findViewById(R.id.fbMessageTextView);
            oImageView = itemView.findViewById(R.id.fbThumbImgView);
            oDateTextView = itemView.findViewById(R.id.fbDateTextView);
            oProfileImage = itemView.findViewById(R.id.profileImage);
            oPageTitle = itemView.findViewById(R.id.fbpageTitle);
            oPlayIcon = itemView.findViewById(R.id.play_icon);
            likes = itemView.findViewById(R.id.likes);
            loves = itemView.findViewById(R.id.loves);
            wows = itemView.findViewById(R.id.wows);
            sad = itemView.findViewById(R.id.sads);
            angry = itemView.findViewById(R.id.angery);
            haha = itemView.findViewById(R.id.haha);
            imageArea = itemView.findViewById(R.id.image_area);
        }
    }
}
