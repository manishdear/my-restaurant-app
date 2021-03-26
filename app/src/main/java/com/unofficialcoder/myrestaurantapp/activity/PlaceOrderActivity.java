package com.unofficialcoder.myrestaurantapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.model.eventBus.SendTotalCashEvent;
import com.unofficialcoder.myrestaurantapp.storage.db.CartItem;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PlaceOrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "PlaceOrderActivity";

    @BindView(R.id.edt_date)
    EditText edt_date;

    @BindView(R.id.txt_total_cash)
    TextView txt_total_cash;

    @BindView(R.id.txt_user_phone)
    TextView txt_user_phone;

    @BindView(R.id.txt_user_address)
    TextView txt_user_address;

    @BindView(R.id.txt_new_address)
    TextView txt_new_address;

    @BindView(R.id.btn_add_new_address)
    Button btn_add_new_address;

    @BindView(R.id.ckb_default_address)
    CheckBox ckb_default_address;

    @BindView(R.id.rdi_cod)
    RadioButton rdi_cod;

    @BindView(R.id.rdi_online_payment)
    RadioButton rdi_online_payment;

    @BindView(R.id.btn_proceed)
    Button btn_proceed;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.pb_loading)
    ProgressBar dialog;

//    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    boolean isSelectedDate = false, isAddNewAddress=false;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        txt_user_phone.setText(Common.currentUser.getUserPhone());
        txt_user_phone.setText(Common.currentUser.getAddress());
        toolbar.setTitle(getString(R.string.place_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_add_new_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAddNewAddress=true;
                ckb_default_address.setChecked(false);

                View layout_add_new_address = LayoutInflater.from(PlaceOrderActivity.this)
                        .inflate(R.layout.layout_add_new_address, null);

                EditText edt_new_address = layout_add_new_address.findViewById(R.id.edt_add_new_address);
                edt_new_address.setText(txt_new_address.getText().toString());
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PlaceOrderActivity.this)
                        .setTitle("Add New Address")
                        .setView(layout_add_new_address)
                        .setNegativeButton("CENCEL", ((dialogInterface, i) -> dialogInterface.dismiss()))
                        .setPositiveButton("ADD", ((dialogInterface, i) -> txt_new_address.setText(edt_new_address.getText().toString())));
                androidx.appcompat.app.AlertDialog addNewAddressDialog = builder.create();
                addNewAddressDialog.show();
            }
        });

        edt_date.setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();

            DatePickerDialog dpd = DatePickerDialog.newInstance(PlaceOrderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
                    );
            dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        });

        btn_proceed.setOnClickListener(view -> {
            if(!isSelectedDate){
                Toast.makeText(PlaceOrderActivity.this, "Please select data", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!isAddNewAddress){
                if(!ckb_default_address.isChecked()){
                    Toast.makeText(PlaceOrderActivity.this, "Please choose default address or set new address", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(rdi_cod.isChecked()){
                //Process COD
                getOrderNumber(false);
            }else if(rdi_online_payment.isChecked()){
                //Process Online Payment
                getOrderNumber(true);
            }
        });
    }

    private void getOrderNumber(boolean isOnlinePayment) {
        dialog.setVisibility(View.VISIBLE);
        if(!isOnlinePayment){
            String address = ckb_default_address.isChecked()?txt_user_address.getText().toString() : txt_new_address.getText().toString();
            MyApplication.compositeDisposable.add(MyApplication.cartDatabase.cartDAO().getAllCart(Common.currentUser.getFbid(),
                    Common.currentRestaurant.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(cartItems -> {

                        //get order number from server
                        MyApplication.compositeDisposable.add(MyApplication.myRestaurantAPI.createOrder(
                                Common.API_KEY,
                                Common.currentUser.getFbid(),
                                Common.currentUser.getUserPhone(),
                                Common.currentUser.getName(),
                                address,
                                edt_date.getText().toString(),
                                Common.currentRestaurant.getId(),
                                "NONE",
                                1,
                                Double.valueOf(txt_total_cash.getText().toString()),
                                cartItems.size())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(createOrderModel -> {
                                    if (createOrderModel.isSuccess()){
                                        MyApplication.compositeDisposable.add(MyApplication.myRestaurantAPI.updateOrder(Common.API_KEY,
                                                String.valueOf(createOrderModel.getResult().get(0).getOrderNumber()),
                                                new Gson().toJson(cartItems))
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(updateOrderModel -> {
                                                    if (updateOrderModel.isSuccess()) {
                                                        MyApplication.cartDatabase.cartDAO().cleanCart (Common.currentUser.getFbid(),
                                                                Common.currentRestaurant.getId())
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(new SingleObserver<Integer>() {
                                                                    @Override
                                                                    public void onSubscribe(Disposable d) {

                                                                    }
                                                                    @Override
                                                                    public void onSuccess(Integer integer) {
                                                                        dialog.setVisibility(View.INVISIBLE);
                                                                        Toast.makeText(PlaceOrderActivity.this, "Order :Placed", Toast.LENGTH_SHORT).show();
                                                                        Intent homeActivity = new Intent(PlaceOrderActivity.this, HomeActivity.class);
                                                                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        startActivity(homeActivity);
                                                                        finish();
                                                                    }

                                                                    @Override
                                                                    public void onError(Throwable e) {
                                                                        Toast.makeText(PlaceOrderActivity.this, "[CLEAR CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }

                                                },throwable -> {
                                                    dialog.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(this, "[UPDATE ORDER]"+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                }));
                                    }
                                    else{
                                        //mDialog.dismiss();
                                        dialog.setVisibility(View.INVISIBLE);
                                        Toast.makeText(this, "[CREATE ORDER]" + createOrderModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }, throwable -> {

                                    dialog.setVisibility(View.INVISIBLE);
                                    Toast.makeText(this, "[CREATE ORDER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }));

                    }, throwable -> {
                        dialog.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "[GET ALL CART]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    })
            );

        }
    }

    private void init() {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isSelectedDate = true;
        edt_date.setText(new StringBuilder("")
        .append(dayOfMonth)
                .append("/")
        .append(monthOfYear+1)
                .append("/")
        .append(year));
    }

    // Event Bus

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
    public void setTotalCash(SendTotalCashEvent event) {
        txt_total_cash.setText(String.valueOf(event.getCash()));
    }

}