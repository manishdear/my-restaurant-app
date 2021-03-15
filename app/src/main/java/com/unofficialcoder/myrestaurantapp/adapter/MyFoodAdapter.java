package com.unofficialcoder.myrestaurantapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Api;
import com.squareup.picasso.Picasso;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.Retrofit.IMyRestaurantAPI;
import com.unofficialcoder.myrestaurantapp.Retrofit.RetrofitClient;
import com.unofficialcoder.myrestaurantapp.activity.FoodDetailsActivity;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.interfaces.FoodDetailOrCardClickListener;
import com.unofficialcoder.myrestaurantapp.model.FoodBean;
import com.unofficialcoder.myrestaurantapp.model.eventBus.FoodDetailEvent;
import com.unofficialcoder.myrestaurantapp.storage.db.CartDataSource;
import com.unofficialcoder.myrestaurantapp.storage.db.CartDatabase;
import com.unofficialcoder.myrestaurantapp.storage.db.CartItem;
import com.unofficialcoder.myrestaurantapp.storage.db.LocalCartDataSource;
import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;
import com.unofficialcoder.myrestaurantapp.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyViewHolder> {

    private static final String TAG = "MyFoodAdapter";

    Context context;
    List<FoodBean> foodList;
    private CompositeDisposable mCompositeDisposable;
    private CartDataSource mCartDataSource;
    private IMyRestaurantAPI mIMyRestaurantAPI;

    public void onStop() {
        mCompositeDisposable.clear();
    }

    public MyFoodAdapter(Context context, List<FoodBean> foodList) {
        this.context = context;
        this.foodList = foodList;
        mCompositeDisposable = new CompositeDisposable();
        mCartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
        mIMyRestaurantAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMyRestaurantAPI.class);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_food_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FoodBean bean = foodList.get(position);

        Picasso.get().load(APIEndPoints.DEMO_IMAGE_SERVER_URL + bean.getImage())
                .placeholder(R.drawable.app_icon)
                .into(holder.img_food);

        holder.text_food_name.setText(bean.getName());
        holder.text_food_price.setText("$" + bean.getPrice());

        //Check Favorite
        if (Common.currentFavOfRestaurant != null && Common.currentFavOfRestaurant.size() > 0){
            if (Common.checkFavorite(bean.getId())){
                holder.img_fav.setImageResource(R.drawable.ic_favorite_primary_24dp);
                holder.img_fav.setTag(true);
            }else {
                holder.img_fav.setImageResource(R.drawable.ic_favorite_border_primary_24dp);
                holder.img_fav.setTag(false);
            }
        }else {
            //Default , all item is no favorite
            holder.img_fav.setTag(false);
        }

        //Event
        holder.img_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick img_fav: "+ v.getTag());
                if ((Boolean)v.getTag()){
                    // if tag = true -> Favorite item click
                    removeFoodFromFavorite(bean);
                    holder.img_fav.setImageResource(R.drawable.ic_favorite_border_primary_24dp);
                    holder.img_fav.setTag(false);
                }else{
                    addFoodToFavorite(bean);
                    holder.img_fav.setImageResource(R.drawable.ic_favorite_primary_24dp);
                    holder.img_fav.setTag(true);
                }
            }
        });
        holder.setListener(new FoodDetailOrCardClickListener() {
            @Override
            public void onFoodItemClickListener(View view, int position, boolean isDetail) {
                if (isDetail){
//                    Toast.makeText(context, "Detail Click", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().postSticky(new FoodDetailEvent(true, foodList.get(position)));
                    context.startActivity(new Intent(context, FoodDetailsActivity.class));
                }else {
                    // Cart create
                    CartItem cartItem = new CartItem();
                    cartItem.setFoodId(foodList.get(position).getId());
                    cartItem.setFoodName(foodList.get(position).getName());
                    cartItem.setFoodPrice(foodList.get(position).getPrice());
                    cartItem.setFoodImage(foodList.get(position).getImage());
                    cartItem.setFoodQuantity(1);
                    cartItem.setUserPhone(Common.currentUser.getUserPhone());
                    cartItem.setRestaurantId(Common.currentRestaurant.getId());
                    cartItem.setFoodAddon("NORMAL");
                    cartItem.setFoodSize("NORMAL");
                    cartItem.setFoodExtraPrice(0.0);
                    cartItem.setFbid(Common.currentUser.getFbid());

                    mCompositeDisposable.add(MyApplication.cartDatabase.cartDAO().insertOrReplaceAll(cartItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                            }, throwable -> {

                            }));
                }
            }
        });

    }

    private void removeFoodFromFavorite(FoodBean bean) {
        StringRequest request = new StringRequest(Request.Method.DELETE, APIEndPoints.DELETE_FOOD_FROM_RESTAURANT+bean.getId()+"&restaurantId="+Common.currentRestaurant.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "removeFoodFromFavorite: " + response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("success")){
                        //Common.removeFavorite(bean.getId());
                        MyUtils.showTheToastMessage(rootObject.getString("message"));
                    }
                }catch (Exception e){
                    Log.e(TAG, "onResponse: ",e );
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, context);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyApplication.mRequestQue.add(request);
    }

    private void addFoodToFavorite(FoodBean bean) {
        StringRequest request = new StringRequest(Request.Method.POST, APIEndPoints.POST_ADD_TO_FAV, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "addFoodToFavorite: "+ response);
                try {
                    JSONObject rootObject = new JSONObject(response);
                    if (rootObject.getBoolean("success")){
                        MyUtils.showTheToastMessage("Added to Favorite!");
                    }

                }catch (Exception e){
                    Log.e(TAG, "onResponse: ", e );
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MyUtils.showVolleyError(error, TAG, context);
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("key", "1234");
                params.put("foodId", bean.getId()+"");
                params.put("restaurantId", Common.currentRestaurant.getId()+"");
                params.put("restaurantName", Common.currentRestaurant.getName());
                params.put("foodName", bean.getName());
                params.put("foodImage", bean.getImage());
                params.put("price", bean.getPrice()+"");
                params.put("fbid", Common.currentUser.getFbid());
                return params;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        MyApplication.mRequestQue.add(request);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_food, img_detail, img_cart, img_fav;
        TextView text_food_name, text_food_price;

        FoodDetailOrCardClickListener listener;

        private void setListener(FoodDetailOrCardClickListener listener){
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_food = itemView.findViewById(R.id.img_food);
            img_detail = itemView.findViewById(R.id.img_detail);
            img_cart = itemView.findViewById(R.id.img_cart);
            img_fav = itemView.findViewById(R.id.img_fav);
            text_food_name = itemView.findViewById(R.id.text_food_name);
            text_food_price = itemView.findViewById(R.id.text_food_price);

           img_detail.setOnClickListener(this);
           img_cart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.img_detail){
                listener.onFoodItemClickListener(v, getAdapterPosition(), true);
            }else{
                listener.onFoodItemClickListener(v, getAdapterPosition(), false);
            }
        }
    }

    public String currentTime(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return ts;
    }


}
