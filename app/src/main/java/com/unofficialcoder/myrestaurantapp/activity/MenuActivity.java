package com.unofficialcoder.myrestaurantapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;
import com.unofficialcoder.myrestaurantapp.Retrofit.IMyRestaurantAPI;
import com.unofficialcoder.myrestaurantapp.Retrofit.RetrofitClient;
import com.unofficialcoder.myrestaurantapp.model.FavoriteOnlyId;
import com.unofficialcoder.myrestaurantapp.storage.db.CartDataSource;
import com.unofficialcoder.myrestaurantapp.storage.db.CartDatabase;
import com.unofficialcoder.myrestaurantapp.storage.db.LocalCartDataSource;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "MenuActivity";

    @BindView(R.id.img_restaurant)
    ImageView img_restaurant;
    @BindView(R.id.recycler_category)
    RecyclerView recycler_category;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton btn_cart;
    @BindView(R.id.badge)
    NotificationBadge badge;

    MyCategoryAdapter adapter;
    List<MenuBean> categoryList;

    private CartDataSource mCartDataSource;

    private IMyRestaurantAPI mIMyRestaurantAPI;
    private LayoutAnimationController mLayoutAnimationController;

    @Override
    protected void onDestroy() {
        MyApplication.compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //countCartByRestaurant();
        //loadFavoriteByRestaurant();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        init();
        initViews();

        countCartByRestaurant();
        loadFavoriteByRestaurant();
    }

    private void countCartByRestaurant() {
        MyApplication.cartDatabase.cartDAO().countItemInCart(Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        badge.setText(String.valueOf(integer));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MenuActivity.this, "[COUNT CART}" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadFavoriteByRestaurant() {
        MyApplication.compositeDisposable.add(
                MyApplication.myRestaurantAPI.getFavoriteByRestaurant(Common.API_KEY,
                        Common.currentUser.getFbid(), Common.currentRestaurant.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(favoriteOnlyIdModel -> {
                            if (favoriteOnlyIdModel.isSuccess()) {
                                if (favoriteOnlyIdModel.getResult() != null && favoriteOnlyIdModel.getResult().size() > 0) {
                                    Common.currentFavOfRestaurant = favoriteOnlyIdModel.getResult();
                                }
                                else {
                                    Common.currentFavOfRestaurant = new ArrayList<>();
                                }
                            }
                            else {
                                Toast.makeText(this, "[GET FAVORITE]"+favoriteOnlyIdModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }, throwable -> {
                            Log.d(TAG, "loadFavoriteByRestaurant: " + throwable.getMessage());
                            Toast.makeText(this, "[GET FAVORITE]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }

    private void initViews() {

        ButterKnife.bind(this);

        mLayoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_from_left);

        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, CartListActivity.class));
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
        //recycler_category.setLayoutAnimation(mLayoutAnimationController);
        recycler_category.setAdapter(adapter);
    }

    private void init() {
        //mDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        mIMyRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT)
                .create(IMyRestaurantAPI.class);

        mCartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
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
            Picasso.get().load(event.getRestaurant().getImage()).into(img_restaurant);
            toolbar.setTitle(event.getRestaurant().getName());

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            // Request Category by restaurant Id
            if(categoryList.isEmpty()){
                MyApplication.compositeDisposable.add(mIMyRestaurantAPI.getCategories(Common.API_KEY, event.getRestaurant().getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(menuModel -> {
                            categoryList.addAll(menuModel.getResult());
                            //recycler_category.setLayoutAnimation(mLayoutAnimationController);
                            adapter.notifyDataSetChanged();

                        }, throwable -> {
                            Toast.makeText(this, "[GET CATEGORY]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
            }

        }
    }
}
