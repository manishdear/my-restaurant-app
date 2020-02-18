package com.unofficialcoder.myrestaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.utils.MyUtils;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.adapter.RestaurantAdapter;
import com.unofficialcoder.myrestaurantapp.adapter.RestaurantSliderAdapter;
import com.unofficialcoder.myrestaurantapp.model.RestaurantBean;
import com.unofficialcoder.myrestaurantapp.services.PicassoImageLoaderService;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ss.com.bannerslider.Slider;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private List<RestaurantBean> restaurantBeanList;
    private RestaurantAdapter restaurantAdapter;

    private TextView textUserName, textUserPhone;

    private DrawerLayout drawer;

    //@BindView(R.id.banner_slider)
    Slider banner_slider;
    //@BindView(R.id.com_accountkit_resend_button)
    RecyclerView recycler_restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //ButterKnife.bind(HomeActivity.this);
        banner_slider = findViewById(R.id.banner_slider);
        recycler_restaurant = findViewById(R.id.recycler_restaurant);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        View headerView = navigationView.getHeaderView(0);
        textUserName = headerView.findViewById(R.id.text_user_name);
        textUserPhone = headerView.findViewById(R.id.text_user_phone);

        textUserName.setText("ManishDear");
        textUserPhone.setText("9111775017");

        initViews();
        loadRestaurant();


    }

    private void initViews() {

        Slider.init(new PicassoImageLoaderService());

        restaurantBeanList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_restaurant.setLayoutManager(layoutManager);
        recycler_restaurant.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()    ));
        restaurantAdapter = new RestaurantAdapter(HomeActivity.this, restaurantBeanList);
        recycler_restaurant.setAdapter(restaurantAdapter);


    }

    private void loadRestaurant(){
        StringRequest otpRequest = new StringRequest(Request.Method.GET, "https://myresproject.herokuapp.com/restaurant?key=1234", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+ response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("success")){
                        JSONArray resultArray = rootObject.getJSONArray("result");
                        if (resultArray.length() != 0){
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject resultObject = resultArray.getJSONObject(i);
                                RestaurantBean restaurant = new RestaurantBean();
                                restaurant.setId(resultObject.getString("id"));
                                restaurant.setName(resultObject.getString("name"));
                                restaurant.setAddress(resultObject.getString("address"));
                                restaurant.setPhone(resultObject.getString("phone"));
                                restaurant.setLat(resultObject.getString("lat"));
                                restaurant.setLng(resultObject.getString("lng"));
                                restaurant.setUserOwner(resultObject.getString("userOwner"));
                                restaurant.setImage(resultObject.getString("image"));
                                restaurant.setPaymentUrl(resultObject.getString("paymentUrl"));
                                restaurantBeanList.add(restaurant);
                            }
                            restaurantAdapter.notifyDataSetChanged();
                            displayBanner();
                        }
                    }

                }catch (Exception e){
                    Log.e(TAG, "onResponse: "+e.toString() );
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, HomeActivity.this);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        otpRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyApplication.mRequestQue.add(otpRequest);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.nav_nearby:
                break;
            case R.id.nav_order_history:
                break;
            case R.id.nav_update_info:
                break;
            case R.id.nav_log_out:
                signOut();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut(){
        AlertDialog confirmDialog = new AlertDialog.Builder(this)
                .setTitle("Sign out")
                .setMessage("Do you really want to sign out?")
                .setNegativeButton("CENCLE", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .create();

        confirmDialog.show();
    }

    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displayBanner(){
        banner_slider.setAdapter(new RestaurantSliderAdapter(restaurantBeanList, HomeActivity.this));
    }
}
