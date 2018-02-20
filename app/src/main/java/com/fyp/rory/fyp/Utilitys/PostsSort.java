package com.fyp.rory.fyp.Utilitys;

import com.fyp.rory.fyp.Models.UserFacebookPost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Rory on 20/02/2018.
 *
 */

public class PostsSort implements Comparator<UserFacebookPost> {

    @Override
    public int compare(UserFacebookPost userFacebookPost, UserFacebookPost t1) {
        Date stringDate = null;
        Date secondDate = null;
        SimpleDateFormat facebookTimeCreatedFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            stringDate = facebookTimeCreatedFormat.parse(userFacebookPost.getTimeCreated());
            secondDate = facebookTimeCreatedFormat.parse(t1.getTimeCreated());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(stringDate.before(secondDate)){
            return 0;
        } else return 1;
       // return stringDate.compareTo(secondDate);
    }
}
