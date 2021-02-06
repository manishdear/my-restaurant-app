package com.unofficialcoder.myrestaurantapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;
import com.unofficialcoder.myrestaurantapp.model.FavoriteOnlyId;
import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.utils.MyUtils;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.adapter.MyCategoryAdapter;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.model.MenuBean;
import com.unofficialcoder.myrestaurantapp.model.eventBus.MenuItemEvent;
import com.unofficialcoder.myrestaurantapp.utils.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    private ImageView img_restaurant;
    private RecyclerView recycler_category;
    private NotificationBadge badge;
    Toolbar toolbar;
    FloatingActionButton btn_cart;

    MyCategoryAdapter adapter;
    List<MenuBean> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initViews();
    }

    private void loadFavoriteByRestaurant(String restaurantId) {
        StringRequest request = new StringRequest(Request.Method.GET, APIEndPoints.GET_FAV_BY_RESTAURANT+restaurantId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "loadFavoriteByRestaurant: "+ response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("success")){
                        Common.currentFavRestaurant = new ArrayList<>();
                        JSONArray resultArray = rootObject.getJSONArray("result");
                        if (resultArray.length() != 0){
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject favObject = resultArray.getJSONObject(i);
                                FavoriteOnlyId fav = new FavoriteOnlyId();
                                fav.setFoodId(favObject.getString("foodId"));
                                Common.currentFavRestaurant.add(fav);
                            }
                        }else {
                            Common.currentFavRestaurant = new ArrayList<>();
                        }
                    }

                }catch (Exception e){
                    Log.e(TAG, "onResponse: ",e );
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, MenuActivity.this);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyApplication.mRequestQue.add(request);
    }

    private void initViews() {
        img_restaurant = findViewById(R.id.img_restaurant);
        recycler_category = findViewById(R.id.recycler_category);
        badge = findViewById(R.id.badge);
        toolbar = findViewById(R.id.toolbar);
        btn_cart = findViewById(R.id.fab);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });


        categoryList = new ArrayList<>();
        adapter = new MyCategoryAdapter(MenuActivity.this, categoryList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter !=  null){
                    switch (adapter.getItemViewType(position)){
                        case Common
                                .DEFAULT_COLUMN_COUNT: return 1;
                        case Common.FULL_WIDTH_COLUMN: return 2;
                        default: return  -1;
                    }
                }else {
                    return -1;
                }
            }
        });

        recycler_category.setLayoutManager(layoutManager);
        recycler_category.addItemDecoration(new SpaceItemDecoration(8));
        recycler_category.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void loadMenuByRestaurant(MenuItemEvent event){

        if (event.isSuccess()){
            Picasso.get().load(APIEndPoints.DEMO_IMAGE_SERVER_URL + "12_curry_mee.jpg").into(img_restaurant);
            toolbar.setTitle(event.getRestaurant().getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            adapter.notifyDataSetChanged();

            loadFavoriteByRestaurant(event.getRestaurant().getId());

            if (categoryList.size() == 0){

                loadMenu(event.getRestaurant().getId());

            }

        }else {

        }
    }

    private void loadMenu(String id){
        StringRequest otpRequest = new StringRequest(Request.Method.GET, APIEndPoints.GET_MENU_BY_RESTAURANT_ID+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "loadMenu: "+ response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("success")){
                        JSONArray resultArray = rootObject.getJSONArray("result");
                        if (resultArray.length() != 0){
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject resultObject = resultArray.getJSONObject(i);
                                MenuBean bean = new MenuBean();
                                bean.setId(resultObject.getString("id"));
                                bean.setName(resultObject.getString("name"));
                                bean.setDescription(resultObject.getString("description"));
                                bean.setImage(resultObject.getString("image"));

                                categoryList.add(bean);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }else{
                        //
                    }

                }catch (Exception e){
                    Log.e(TAG, "onResponse: "+e.toString() );
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, MenuActivity.this);
            }
        }){
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("key", "1234");
//                params.put("restaurantId", Common.currentRestaurant.getId());
//                return params;
//            }
        };
        otpRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyApplication.mRequestQue.add(otpRequest);
    }
}
