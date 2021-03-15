package com.unofficialcoder.myrestaurantapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.Retrofit.IMyRestaurantAPI;
import com.unofficialcoder.myrestaurantapp.Retrofit.RetrofitClient;
import com.unofficialcoder.myrestaurantapp.adapter.MyAddonAdapter;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.model.FoodBean;
import com.unofficialcoder.myrestaurantapp.model.Size;
import com.unofficialcoder.myrestaurantapp.model.eventBus.AddOnEventChange;
import com.unofficialcoder.myrestaurantapp.model.eventBus.AddonLoadEvent;
import com.unofficialcoder.myrestaurantapp.model.eventBus.FoodDetailEvent;
import com.unofficialcoder.myrestaurantapp.model.eventBus.SizeLoadEvent;
import com.unofficialcoder.myrestaurantapp.storage.db.CartDataSource;
import com.unofficialcoder.myrestaurantapp.storage.db.CartDatabase;
import com.unofficialcoder.myrestaurantapp.storage.db.CartItem;
import com.unofficialcoder.myrestaurantapp.storage.db.LocalCartDataSource;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FoodDetailsActivity extends AppCompatActivity {

    private static final String TAG = "FoodDetailsActivity";

    @BindView(R.id.fab_add_to_cart)
    FloatingActionButton fab_add_to_cart;
    @BindView(R.id.btn_view_cart)
    Button btn_view_cart;
    @BindView(R.id.price)
    TextView txt_money;
    @BindView(R.id.rdi_group_size)
    RadioGroup rdi_group_size;
    @BindView(R.id.recycler_addon)
    RecyclerView recycler_addon;
    @BindView(R.id.txt_description)
    TextView txt_description;
    @BindView(R.id.img_food_detail)
    ImageView img_food_detail;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private IMyRestaurantAPI mIMyRestaurantAPI;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private AlertDialog mDialog;
    private CartDataSource mCartDataSource;

    private FoodBean selectedFood;
    private Double originalPrice;

    private double sizePrice = 0.0;
    private String sizeSelected;
    private double addOnPrice = 0.0;
    private double extraPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        init();
        initViews();
    }

    private void initViews() {

        ButterKnife.bind(this);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fab_add_to_cart.setOnClickListener(v -> {
            CartItem cartItem = new CartItem();
            cartItem.setFoodId(selectedFood.getId());
            cartItem.setFoodName(selectedFood.getName());
            cartItem.setFoodPrice(selectedFood.getPrice());
            cartItem.setFoodImage(selectedFood.getImage());
            cartItem.setFoodQuantity(1);
            cartItem.setUserPhone(Common.currentUser.getUserPhone());
            cartItem.setRestaurantId(Common.currentRestaurant.getId());
            cartItem.setFoodAddon(new Gson().toJson(Common.addonList));
            cartItem.setFoodSize(sizeSelected);
            cartItem.setFoodExtraPrice(extraPrice);
            cartItem.setFbid(Common.currentUser.getFbid());

            mCompositeDisposable.add(mCartDataSource.insertOrReplaceAll(cartItem)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }, throwable -> {
                        Toast.makeText(this, "[ADD CART]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }));
        });

        btn_view_cart.setOnClickListener(v -> {
            startActivity(new Intent(FoodDetailsActivity.this, CartListActivity.class));
        });

    }

    private void init() {
        Log.d(TAG, "init: called!!");
        mCartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
//        mDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        mIMyRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT)
                .create(IMyRestaurantAPI.class);
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

    // Event Register / Unregister
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void displayFoodDetail(FoodDetailEvent event) {
        Log.d(TAG, "displayFoodDetail: called!!");
        Log.d(TAG, "displayFoodDetail: Name: " + event.getFood().getName());
        if (event.isSuccess()) {
            toolbar.setTitle(event.getFood().getName());

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            selectedFood = event.getFood();
            originalPrice = event.getFood().getPrice();

            txt_money.setText(String.valueOf(originalPrice));
            txt_description.setText(event.getFood().getDescription());
            Picasso.get().load(event.getFood().getImage()).into(img_food_detail);

            if (event.getFood().isSize() && event.getFood().isAddon()) {
                // Load size and addon from server
                mDialog.show();
                mCompositeDisposable.add(mIMyRestaurantAPI.getSizeOfFood(Common.API_KEY, event.getFood().getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(sizeModel -> {
                            // Send local event bust
                            EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));

                            // Load addon after load size
                            mDialog.show();
                            mCompositeDisposable.add(mIMyRestaurantAPI.getAddonOfFood(Common.API_KEY, event.getFood().getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(addonModel -> {
                                        mDialog.dismiss();
                                        EventBus.getDefault().post(new AddonLoadEvent(true, addonModel.getResult()));
                                    }, throwable -> {
                                        Toast.makeText(this, "[LOAD ADDON]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));

                        }, throwable -> {
                            mDialog.dismiss();
                            Toast.makeText(this, "[LOAD SIZE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
            } else {
                // If food only have size
                if (event.getFood().isSize()) {
                    mCompositeDisposable.add(mIMyRestaurantAPI.getSizeOfFood(Common.API_KEY, event.getFood().getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(sizeModel -> {
                                // Send local event bust
                                EventBus.getDefault().post(new SizeLoadEvent(true, sizeModel.getResult()));
                            }, throwable -> {
                                mDialog.dismiss();
                                Toast.makeText(this, "[LOAD SIZE]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
                }

                // If food only have  addon
                if (event.getFood().isAddon()) {
                    mCompositeDisposable.add(mIMyRestaurantAPI.getAddonOfFood(Common.API_KEY, event.getFood().getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(addonModel -> {
                                mDialog.dismiss();
                                Log.d(TAG, "displayFoodDetail: "+addonModel.getResult().size());
                                EventBus.getDefault().post(new AddonLoadEvent(true, addonModel.getResult()));
                            }, throwable -> {
                                Toast.makeText(this, "[LOAD ADDON]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }));
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displaySize(SizeLoadEvent event) {
        Log.d(TAG, "displaySize: called!!");
        if (event.isSuccess()) {
            // Create radio button base on size length
            for (Size size : event.getSizeList()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked)
                        sizePrice = size.getExtraPrice();

                    calculatePrice();
                    sizeSelected = size.getDescription();
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
                radioButton.setLayoutParams(params);
                radioButton.setText(size.getDescription());
                radioButton.setTag(size.getExtraPrice());

                rdi_group_size.addView(radioButton);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayAddon(AddonLoadEvent event) {
        Log.d(TAG, "displayAddon: called!!");
        Log.d(TAG, "displayAddon: "+event.getAddonList().get(0).toString());
        if (event.isSuccess()) {
            recycler_addon.setHasFixedSize(true);
            recycler_addon.setLayoutManager(new LinearLayoutManager(this));
            recycler_addon.setAdapter(new MyAddonAdapter(FoodDetailsActivity.this, event.getAddonList()));
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void priceChange(AddOnEventChange eventChange) {
        Log.d(TAG, "priceChange: called!!");
        if (eventChange.isAdd()) {
            addOnPrice += eventChange.getAddon().getExtraPrice();
        } else {
            addOnPrice -= eventChange.getAddon().getExtraPrice();
        }

        calculatePrice();
    }

    private void calculatePrice() {
        Log.d(TAG, "calculatePrice: called!!");
        extraPrice = 0.0;
        double newPrice;

        extraPrice += sizePrice;
        extraPrice += addOnPrice;

        newPrice = originalPrice + extraPrice;

        txt_money.setText(String.valueOf(newPrice));
    }

}
