package com.fyp.rory.fyp.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fyp.rory.fyp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class Login_Activity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private ProgressBar prog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        prog = (ProgressBar)findViewById(R.id.progressBar2);

        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions("public_profile","user_posts","user_friends");
        LoginManager.getInstance().logInWithPublishPermissions(
                this,
                Arrays.asList("publish_actions"));
        //loginButton.setPublishPermissions("publish_actions");

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Firebase", "OnComplete : " +task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("Firebase", "Failed : ", task.getException());
                            Toast.makeText(Login_Activity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        } else {
                            prog.setVisibility(View.GONE);
                            loginButton.setVisibility(View.VISIBLE);
                        }


                    }
                });

        callbackManager = CallbackManager.Factory.create();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Intent mainIntent = new Intent(Login_Activity.this, MainActivity.class);
                Login_Activity.this.startActivity(mainIntent);
                finish();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("Facebook failed",exception.toString());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }

}
