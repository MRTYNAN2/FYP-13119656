package com.fyp.rory.fyp.Activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fyp.rory.fyp.R;

public class User_Post_by_Graph extends AppCompatActivity {

//    POST graph.facebook.com
//  /{user-id}/feed?
//    message={message}&
//    access_token={access-token}

    private EditText messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__post_by__graph);
        Button post = (Button)findViewById(R.id.post_button);
        messageText = (EditText)findViewById(R.id.postMessageText);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!messageText.getText().toString().equalsIgnoreCase("")){
                    postUsingGraph(messageText.getText().toString());
                } else {

                }
            }
        });
    }

    public void postUsingGraph(String postMessage){
        Bundle params = new Bundle();
        params.putString("message", "This is a test message");
        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        Log.d("FBPOST","POst Sucessful");
                        messageText.setText("");
                    }
                }
        ).executeAsync();

    }
}
