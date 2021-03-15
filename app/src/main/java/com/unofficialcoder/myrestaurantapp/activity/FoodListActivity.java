package com.unofficialcoder.myrestaurantapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.adapter.MyFoodAdapter;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.model.FoodBean;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class FoodListActivity extends AppCompatActivity {

    private static final String TAG = "FoodListActivity";

    @BindView(R.id.img_category)
    ImageView img_category;
    @BindView(R.id.recycler_foot_list)
    RecyclerView recycler_foot_list;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    MyFoodAdapter adapter, searchAdapter;
    List<FoodBean> foodBeanList, searchFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        initViews();
    }

    private void initViews() {
        ButterKnife.bind(this);
        foodBeanList = new ArrayList<>();
        searchFoodList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(FoodListActivity.this, RecyclerView.VERTICAL, false);
        recycler_foot_list.setLayoutManager(layoutManager);
        recycler_foot_list.addItemDecoration(new DividerItemDecoration(FoodListActivity.this,layoutManager.getOrientation() ));

        adapter = new MyFoodAdapter(FoodListActivity.this, foodBeanList);
        recycler_foot_list.setAdapter(adapter);
        searchAdapter = new MyFoodAdapter(FoodListActivity.this, searchFoodList);

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

    @Override
    protected void onDestroy() {

        if (adapter != null){
            //adapter.onStop();
        }
        if (searchAdapter != null){
            //searchAdapter.onStop();
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //Event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearchFood(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                //Restore to original adapter when user close search
                recycler_foot_list.setAdapter(adapter);
                return true;
            }
        });

        return true;
    }

    private void startSearchFood(String query) {
        //dialog.show;
        searchFoodList.clear();
        searchFoodByName(query);
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

            if (foodBeanList.size() == 0){
                loadMenuByResId(event.getCategory().getId());
            }

        }else{

        }
    }

    private void loadMenuByResId(int menuId){
        MyApplication.compositeDisposable.add(
                MyApplication.myRestaurantAPI.getFoodById(Common.API_KEY, menuId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( menu->
                        {
                            if(menu.isSuccess()){
                                foodBeanList.addAll(menu.getResult());
                                adapter.notifyDataSetChanged();
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "[GET CATEGORY]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "loadMenuByResId: " + throwable.getMessage());
                        }
                )
        );
    }

//    private void loadMenu(String id){
//        StringRequest otpRequest = new StringRequest(Request.Method.GET, APIEndPoints.GET_FOOD_BY_MENU_ID+id, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "onResponse: "+ response);
//                try {
//                    JSONObject rootObject = new JSONObject(response);
//                    if (rootObject.getBoolean("success")){
//                        JSONArray resultArray = rootObject.getJSONArray("result");
//                        if (resultArray.length() != 0){
//                            for (int i = 0; i < resultArray.length(); i++) {
//                                JSONObject resultObject = resultArray.getJSONObject(i);
//                                FoodBean bean = new FoodBean();
//                                bean.setId(resultObject.getString("id"));
//                                bean.setName(resultObject.getString("name"));
//                                bean.setDescription(resultObject.getString("description"));
//                                bean.setImage(resultObject.getString("image"));
//                                bean.setPrice(resultObject.getString("price"));
//                                bean.setSize(resultObject.getBoolean("isSize"));
//                                bean.setAddon(resultObject.getBoolean("isAddon"));
//                                bean.setDiscount(resultObject.getString("discount"));
//
//                                foodBeanList.add(bean);
//                            }
//                            adapter.notifyDataSetChanged();
//                        }
//                    }else{
//                        //
//                    }
//
//                }catch (Exception e){
//                    Log.e(TAG, "onResponse: "+e.toString() );
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                MyUtils.showVolleyError(error, TAG, FoodListActivity.this);
//            }
//        }){
////            @Override
////            protected Map<String, String> getParams() {
////                Map<String, String> params = new HashMap<>();
////                params.put("key", "1234");
////                params.put("restaurantId", Common.currentRestaurant.getId());
////                return params;
////            }
//        };
//        otpRequest.setRetryPolicy(new DefaultRetryPolicy(
//                30000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        ));
//        MyApplication.mRequestQue.add(otpRequest);
//    }

    private void searchFoodByName(String name){
        MyApplication.compositeDisposable.add(
                MyApplication.myRestaurantAPI.getFoodByName(Common.API_KEY, name)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(food -> {
                    if(food.isSuccess()){
                        searchFoodList.addAll(food.getResult());
                        recycler_foot_list.setAdapter(searchAdapter);
                        searchAdapter.notifyDataSetChanged();
                    }
                        },
                        throwable -> {
                            Log.d(TAG, "searchFoodByName: " + throwable.getMessage());
                        })

        );
    }
//    private void searchFood(String name){
//        StringRequest request = new StringRequest(Request.Method.GET, APIEndPoints.SEARCH_FOOD+name, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d(TAG, "onResponse: "+ response);
//                try {
//                    JSONObject rootObject = new JSONObject(response);
//                    if (rootObject.getBoolean("success")){
//                        JSONArray resultArray = rootObject.getJSONArray("result");
//                        if (resultArray.length() != 0){
//                            for (int i = 0; i < resultArray.length(); i++) {
//                                JSONObject resultObject = resultArray.getJSONObject(i);
//                                FoodBean bean = new FoodBean();
//                                bean.setId(resultObject.getString("id"));
//                                bean.setName(resultObject.getString("name"));
//                                bean.setDescription(resultObject.getString("description"));
//                                bean.setImage(resultObject.getString("image"));
//                                bean.setPrice(resultObject.getString("price"));
//                                bean.setSize(resultObject.getBoolean("isSize"));
//                                bean.setAddon(resultObject.getBoolean("isAddon"));
//                                bean.setDiscount(resultObject.getString("discount"));
//
//                                searchFoodList.add(bean);
//                            }
//                            recycler_foot_list.setAdapter(searchAdapter);
//                            searchAdapter.notifyDataSetChanged();
//                        }
//                    }else{
//                        //
//                    }
//
//                }catch (Exception e){
//                    Log.e(TAG, "onResponse: "+e.toString() );
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                MyUtils.showVolleyError(error, TAG, FoodListActivity.this);
//            }
//        });
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                30000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        ));
//        MyApplication.mRequestQue.add(request);
//    }
}
