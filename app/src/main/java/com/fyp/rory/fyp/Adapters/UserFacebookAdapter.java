package com.fyp.rory.fyp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private String facebookName;
    private String faceBookPageID;

    public UserFacebookAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public UserFacebookAdapter(Context mContext, String clientPageName, String facebookPageID) {
        this.mContext = mContext;
        this.facebookName = clientPageName;
        this.faceBookPageID = facebookPageID;
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

        ViewHolder vh = new ViewHolder(layoutView);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final UserFacebookPost post = mItems.get(position);
        //this gets Picasso to handle grabbing the profile picture of the facebook account
        if (faceBookPageID != null)
        Picasso.with(mContext)
                .load("https://graph.facebook.com/me/picture?type=normal")
                .into(holder.oProfileImage);
        // there is not call for user name hardcoded above
        if (facebookName != null)
        holder.oPageTitle.setText(facebookName);
        else
            holder.oPageTitle.setVisibility(View.GONE);

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
                } else {
                    //holder.oMessageTextView.setMaxLines(8);
                }
            }
        });

        // if there is video source display play button. soucre is a raw link to video (In case need or implement video in future)
        if (!post.getmVideoSoucre().equalsIgnoreCase("null")) {
            holder.oPlayIcon.setVisibility(View.VISIBLE);
        } else {
            holder.oPlayIcon.setVisibility(View.GONE);

        }
        // post without images are ignored but just in case. again simple on click on image to go to link.
        if (!post.getPicture().isEmpty()) {
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
            holder.oImageView.setVisibility(View.GONE);
        }

        // This is the format that we receive the date_created field
        SimpleDateFormat facebookTimeCreatedFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        //This is the format facebook displays dates on site "/" this is to be replaced with "at"
        SimpleDateFormat outputDate = new SimpleDateFormat("d MMMM yyyy / HH:mm");
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

        //this is the top of the screen (profile pic, Name , date) same click on go to link
        holder.oWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post.getLink().isEmpty()) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(post.getLink()));
                    mContext.startActivity(i);
                }
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View oWrapper;
        public TextView oMessageTextView;
        public TextView oPageTitle;
        public ImageView oImageView;
        public TextView oDateTextView;
        public ImageView oProfileImage;
        public ImageView oPlayIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            oWrapper = itemView.findViewById(R.id.fbItemWrapper);
            oMessageTextView = (TextView) itemView.findViewById(R.id.fbMessageTextView);
            oImageView = (ImageView) itemView.findViewById(R.id.fbThumbImgView);
            oDateTextView = (TextView) itemView.findViewById(R.id.fbDateTextView);
            oProfileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            oPageTitle = (TextView) itemView.findViewById(R.id.fbpageTitle);
            oPlayIcon = (ImageView) itemView.findViewById(R.id.play_icon);
        }
    }
}
