package com.fyp.rory.fyp.Models;

public class UserFacebookPost
{
    private String mID;
    private String mLink;
    private String mPicture;
    private String mMessage;
    private String mTimeCreated;
    private String mVideoSource;

    public UserFacebookPost(String id, String link, String picture, String message, String timeCreated, String videoSoucre)
    {
        mID = id;
        mLink = link;
        mPicture = picture;
        mMessage = message;
        mTimeCreated = timeCreated;
        mVideoSource = videoSoucre;
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
}