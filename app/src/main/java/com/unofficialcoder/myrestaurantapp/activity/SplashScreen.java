package com.unofficialcoder.myrestaurantapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.Retrofit.IMyRestaurantAPI;
import com.unofficialcoder.myrestaurantapp.Retrofit.RetrofitClient;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.storage.MySharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    AlertDialog dialog;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new AlertDialog.Builder(SplashScreen.this).setCancelable(false).create();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                loadFineLocation();
            }
        }, 1000);
    }

    private void loadFineLocation() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                        if(isLoggedIn) {

                            dialog.show();
                            compositeDisposable.add(MyApplication.myRestaurantAPI.getUser(Common.API_KEY, accessToken.getUserId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            userModel -> {
                                                if(userModel.isSuccess()){
                                                    Common.currentUser = userModel.getResult().get(0);
                                                    startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                                                    finish();
                                                }else{
                                                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                                    finish();
                                                }
                                                dialog.dismiss();
                                            },
                                            throwable -> {
                                                dialog.dismiss();
                                                Toast.makeText(SplashScreen.this, "[GET USER API] "
                                                        +throwable.getMessage(), Toast.LENGTH_SHORT ).show();
                                            })
                            );
                        }
                        else {
                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        Toast.makeText(SplashScreen.this, "You must accept this permission to use this app",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEY_HASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
