package com.unofficialcoder.myrestaurantapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.adapter.MyFavoriteAdapter;
import com.unofficialcoder.myrestaurantapp.adapter.MyFoodAdapter;
import com.unofficialcoder.myrestaurantapp.model.FavoriteBean;
import com.unofficialcoder.myrestaurantapp.model.FoodBean;
import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;
import com.unofficialcoder.myrestaurantapp.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    private static final String TAG = "FavoriteActivity";

    List<FavoriteBean> favoriteList;
    RecyclerView recycler_fav;
    Toolbar toolbar;
    MyFavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initViews();
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

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycler_fav = findViewById(R.id.recycler_fav);

        favoriteList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(FavoriteActivity.this, RecyclerView.VERTICAL, false);
        recycler_fav.setLayoutManager(layoutManager);
        recycler_fav.addItemDecoration(new DividerItemDecoration(FavoriteActivity.this,layoutManager.getOrientation() ));
        adapter = new MyFavoriteAdapter(FavoriteActivity.this, favoriteList);
        recycler_fav.setAdapter(adapter);
        loadFavoriteItems();
    }

    private void loadFavoriteItems(){
        StringRequest request = new StringRequest(Request.Method.GET, APIEndPoints.GET_FAVORITE_FOOD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "loadFavoriteItems: "+ response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("success")){
                        JSONArray resultArray = rootObject.getJSONArray("result");
                        if (resultArray.length() != 0){
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject resultObject = resultArray.getJSONObject(i);
                                FavoriteBean bean = new FavoriteBean();
                                bean.setFbid(resultObject.getString("fbid"));
                                bean.setFoodId(resultObject.getString("foodId"));
                                bean.setRestaurantId(resultObject.getString("restaurantId"));
                                bean.setRestaurantName(resultObject.getString("restaurantName"));
                                bean.setFoodName(resultObject.getString("foodName"));
                                bean.setFoodImage(resultObject.getString("foodImage"));
                                bean.setPrice(resultObject.getString("price"));
                                favoriteList.add(bean);
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
                MyUtils.showVolleyError(error, TAG, FavoriteActivity.this);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyApplication.mRequestQue.add(request);
    }
}
