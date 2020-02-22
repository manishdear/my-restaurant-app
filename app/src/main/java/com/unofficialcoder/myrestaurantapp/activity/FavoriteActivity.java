package com.unofficialcoder.myrestaurantapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.adapter.MyFoodAdapter;
import com.unofficialcoder.myrestaurantapp.model.FoodBean;
import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;
import com.unofficialcoder.myrestaurantapp.utils.MyUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private static final String TAG = "FavoriteActivity";

    RecyclerView recycler_fav;
    Toolbar toolbar;

    MyFoodAdapter adapter, searchAdapter;
    List<FoodBean> foodBeanList, searchFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initViews();
    }

    private void initViews() {
        foodBeanList = new ArrayList<>();
        searchFoodList = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar);
        recycler_fav = findViewById(R.id.recycler_fav);

        LinearLayoutManager layoutManager = new LinearLayoutManager(FavoriteActivity.this, RecyclerView.VERTICAL, false);
        recycler_fav.setLayoutManager(layoutManager);
        recycler_fav.addItemDecoration(new DividerItemDecoration(FavoriteActivity.this,layoutManager.getOrientation() ));

        adapter = new MyFoodAdapter(FavoriteActivity.this, foodBeanList);
        recycler_fav.setAdapter(adapter);
        searchAdapter = new MyFoodAdapter(FavoriteActivity.this, searchFoodList);
    }

    private void searchFood(String name){
        StringRequest request = new StringRequest(Request.Method.GET, APIEndPoints.SEARCH_FOOD+name, new Response.Listener<String>() {
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
                                FoodBean bean = new FoodBean();
                                bean.setId(resultObject.getString("id"));
                                bean.setName(resultObject.getString("name"));
                                bean.setDescription(resultObject.getString("description"));
                                bean.setImage(resultObject.getString("image"));
                                bean.setPrice(resultObject.getString("price"));
                                bean.setIsSize(resultObject.getString("isSize"));
                                bean.setIsAddon(resultObject.getString("isAddon"));
                                bean.setDiscount(resultObject.getString("discount"));

                                searchFoodList.add(bean);
                            }
                            recycler_fav.setAdapter(searchAdapter);
                            searchAdapter.notifyDataSetChanged();
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
