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
import com.bumptech.glide.Glide;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.adapter.MyFoodAdapter;
import com.unofficialcoder.myrestaurantapp.model.FoodBean;
import com.unofficialcoder.myrestaurantapp.model.MenuBean;
import com.unofficialcoder.myrestaurantapp.model.eventBus.FoodListEvent;
import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;
import com.unofficialcoder.myrestaurantapp.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    private static final String TAG = "FoodListActivity";

    ImageView img_category;
    RecyclerView recycler_foot_list;
    Toolbar toolbar;

    MyFoodAdapter adapter;

    List<FoodBean> foodBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        initViews();
    }

    private void initViews() {

        foodBeanList = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar);
        img_category = findViewById(R.id.img_category);
        recycler_foot_list = findViewById(R.id.recycler_foot_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(FoodListActivity.this, RecyclerView.VERTICAL, false);
        recycler_foot_list.setLayoutManager(layoutManager);
        recycler_foot_list.addItemDecoration(new DividerItemDecoration(FoodListActivity.this,layoutManager.getOrientation() ));

        adapter = new MyFoodAdapter(FoodListActivity.this, foodBeanList);
        recycler_foot_list.setAdapter(adapter);
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
    public void loadFoodListByCategory(FoodListEvent event){

        if (event.isSuccess()){
            Glide.with(FoodListActivity.this)
                    .load(APIEndPoints.DEMO_IMAGE_SERVER_URL + event.getCategory().getImage())
                    .into(img_category);
            toolbar.setTitle(event.getCategory().getName());
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            loadMenu(event.getCategory().getId());

        }else{

        }
    }

    private void loadMenu(String id){
        StringRequest otpRequest = new StringRequest(Request.Method.GET, APIEndPoints.GET_FOOD_BY_MENU_ID+id, new Response.Listener<String>() {
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

                                foodBeanList.add(bean);
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
                MyUtils.showVolleyError(error, TAG, FoodListActivity.this);
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