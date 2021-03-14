package com.unofficialcoder.myrestaurantapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

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

    AlertDialog dialog;
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
                    now.get(Calendar.DAY_OF_MONTH));
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
            }else if(rdi_online_payment.isChecked()){
                //Process Online Payment
            }
        });
    }

    private void init() {
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isSelectedDate = true;
        edt_date.setText(new StringBuilder("")
        .append(dayOfMonth)
                .append("/")
        .append(monthOfYear)
                .append("/")
        .append(year));
    }
}