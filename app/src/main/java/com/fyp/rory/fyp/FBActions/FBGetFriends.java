package com.fyp.rory.fyp.FBActions;

import android.content.Context;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fyp.rory.fyp.Activitys.MainActivity;
import com.fyp.rory.fyp.Models.FriendList;
import com.fyp.rory.fyp.Models.UserFriendsID;
import com.fyp.rory.fyp.Utilitys.FBPreferences;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Rory on 20/02/2018.
 *
 */

public class FBGetFriends {
    private Context context;

    public void getAllFriends(Context context){
        this.context = context;
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),"/me/friends?",null, HttpMethod.GET,new GraphRequest.Callback(){
            @Override
            public void onCompleted(GraphResponse response) {
                if (response != null) {
                    String responseStr = response.getJSONObject().toString();
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(responseStr);
                    if (element != null && element.isJsonObject() && !element.isJsonNull()) {
                        JsonObject data = element.getAsJsonObject();
                        JsonElement jsonArray = data.get("data");
                        if (jsonArray.isJsonArray() && !jsonArray.isJsonNull()) {
                            JsonArray array = jsonArray.getAsJsonArray();
                            for (JsonElement object : array) {
                                if (object != null && object.isJsonObject() && !object.isJsonNull()) {
                                    JsonObject obj = object.getAsJsonObject();
                                    JsonElement id = obj.get("id");
                                    JsonElement name = obj.get("name");

                                    UserFriendsID friend = new UserFriendsID(id.getAsString(),name.getAsString());
                                    FriendList.getInstance().addUser(friend);
                                }
                            }
                        }
                        getFriends();
                    }
                }
            }
        }
        ).executeAsync();
    }

    private void getFriends() {
        if (FBPreferences.getInstance().getPref(context) == true) {
            MainActivity.notifyFriendsListFinished();
        } else {
            MainActivity.notifyNewUser();
            FBPreferences.getInstance().setPref(context);
        }

    }

}
