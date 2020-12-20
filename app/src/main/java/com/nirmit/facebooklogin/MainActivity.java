package com.nirmit.facebooklogin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.shantanudeshmukh.linkedinsdk.LinkedInBuilder;
import com.shantanudeshmukh.linkedinsdk.helpers.LinkedInUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final int LINKEDIN_REQUEST_CODE = 99;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private ImageView tvImg;
    private TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //facebook
        loginButton = findViewById(R.id.login_button);
        tvImg = findViewById(R.id.tvImg);
        tvText = findViewById(R.id.tvText);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setPermissions(Arrays.asList("email,public_profile"));
        LoginManager.getInstance().logOut();

        //linkidn

//       // btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                LinkedInBuilder.getInstance(MainActivity.this)
//                        .setClientID("86cb7dc3vpjcv5")
//                        .setClientSecret("yZHWWoFS8091IJkx")
//                        .setRedirectURI("https://www.linkedin.com/feed/")
//                        .authenticate(LINKEDIN_REQUEST_CODE);
//
//            }
//        });
//


        //linkdinover

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("demo", "login successful");
            }

            @Override
            public void onCancel() {
                Log.d("demo", "login cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("demo", "meet error" + error.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @SuppressLint("WrongConstant")
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("demo",object.toString());
                        try {
                            String name = object.getString("name");
                            String id = object.getString("id");
                            tvText.setText(name);

                            String imageURL = "https://graph.facebook.com/"+ id + "/picture?type=large";
                            Glide.with(MainActivity.this).load(imageURL).error(R.drawable.com_facebook_button_icon).into(tvImg);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString("fields","picture.type(large),gender,name,id,first_name,last_name");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
        
        //linkdin

        if (requestCode == LINKEDIN_REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                //Successfully signed in
                LinkedInUser user = data.getParcelableExtra("social_login");

                //acessing user info
                Log.i("LinkedInLogin", user.getFirstName());

            } else {

                if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_USER_DENIED) {
                    //Handle : user denied access to account

                } else if (data.getIntExtra("err_code", 0) == LinkedInBuilder.ERROR_FAILED) {

                    //Handle : Error in API : see logcat output for details
                    Log.e("LINKEDIN ERROR", data.getStringExtra("err_message"));
                }
            }
        }


    }

    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken == null)
            {
                LoginManager.getInstance().logOut();
                tvText.setText("");
                tvImg.setImageResource(0);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tokenTracker.stopTracking();

    }
    public void disconnectFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
    }



}