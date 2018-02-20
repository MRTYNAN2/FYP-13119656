package com.fyp.rory.fyp.Models;

public class UserFacebookPost
{
    public UserFacebookPost(){}

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    private String mName = "";
    private String mProfileImage;
    private String mID;
    private String mLink = "";
    private String mPicture;
    private String mMessage = "";
    private String mTimeCreated;
    private String mVideoSource = "";
    private FBReactions mReactions;

    public UserFacebookPost(String name, String profileImage, String id, String link, String picture, String message, String timeCreated, String videoSoucre, FBReactions reactions)
    {
        mName = name;
        mProfileImage = profileImage;
        mID = id;
        mLink = link;
        mPicture = picture;
        mMessage = message;
        mTimeCreated = timeCreated;
        mVideoSource = videoSoucre;
        mReactions = reactions;
    }

    public String getPicture()
    {
        return mPicture;
    }

    public void setPicture(String mPicture)
    {
        this.mPicture = mPicture;
    }

    public String getLink()
    {
        return mLink;
    }

    public void setLink(String mLink)
    {
        this.mLink = mLink;
    }

    public String getMessage()
    {
        return mMessage;
    }

    public void setMessage(String mMessage)
    {
        this.mMessage = mMessage;
    }

    public String getTimeCreated()
    {
        return mTimeCreated;
    }

    public void setTimeCreated(String mTimeCreated)
    {
        this.mTimeCreated = mTimeCreated;
    }

    public String getmVideoSoucre() {
        if (mVideoSource == null)
        {
            return "null";
        }
        return mVideoSource;
    }

    public void setmVideoSoucre(String mVideoSoucre) {
        this.mVideoSource = mVideoSoucre;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getmProfileImage() {
        return mProfileImage;
    }

    public void setmProfileImage(String mProfileImage) {
        this.mProfileImage = mProfileImage;
    }

    public FBReactions getmReactions() {
        return mReactions;
    }

    public void setmReactions(FBReactions mReactions) {
        this.mReactions = mReactions;
    }
}
