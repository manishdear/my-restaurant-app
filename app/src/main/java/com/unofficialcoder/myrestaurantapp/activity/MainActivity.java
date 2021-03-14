package com.unofficialcoder.myrestaurantapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    CallbackManager callbackManager;
    Button btnSignin;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignin = findViewById(R.id.btn_sign_in);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "Name"));
            }
        });

        String EMAIL = "email";
        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    String name = object.getString("name");
                                    String email = object.getString("email");
                                    compositeDisposable.add(MyApplication.myRestaurantAPI.updateUserInfo(Common.API_KEY,
                                            accessToken.getUserId(),
                                            email, name, "address" )
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    updateUserModel -> {
                                                        if(updateUserModel.isSuccess()){

                                                            compositeDisposable.add(MyApplication.myRestaurantAPI.getUser(Common.API_KEY, accessToken.getUserId())
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe(
                                                                            userModel -> {
                                                                                if(userModel.isSuccess()){
                                                                                    Common.currentUser = userModel.getResult().get(0);
                                                                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                                                    finish();
                                                                                }else{
                                                                                    Toast.makeText(MainActivity.this, "[GET USER RESULT] "
                                                                                            +userModel.getMessage(), Toast.LENGTH_SHORT ).show();
                                                                                }
                                                                            },
                                                                            throwable -> {
                                                                                Toast.makeText(MainActivity.this, "[GET USER] "
                                                                                        +throwable.getMessage(), Toast.LENGTH_SHORT ).show();
                                                                            })
                                                            );
                                                        }else{
                                                            Toast.makeText(MainActivity.this, updateUserModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    },
                                                    throwable -> {
                                                        Toast.makeText(MainActivity.this, ""
                                                                +throwable.getMessage(), Toast.LENGTH_SHORT ).show();
                                                    })
                                    );

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("LOGIN", "cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("LOGIN", exception.toString());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}