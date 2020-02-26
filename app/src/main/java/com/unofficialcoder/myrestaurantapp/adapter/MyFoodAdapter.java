package com.unofficialcoder.myrestaurantapp.adapter;

import android.content.Context;
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
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.interfaces.FoodDetailOrCardClickListener;
import com.unofficialcoder.myrestaurantapp.model.FoodBean;
import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;
import com.unofficialcoder.myrestaurantapp.utils.MyUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyViewHolder> {

    private static final String TAG = "MyFoodAdapter";

    Context context;
    List<FoodBean> foodList;

    public MyFoodAdapter(Context context, List<FoodBean> foodList) {
        this.context = context;
        this.foodList = foodList;
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
        if (Common.currentFavRestaurant != null && Common.currentFavRestaurant.size() > 0){

            if (Common.checkFavorite(bean.getId(), Common.currentFavRestaurant)){
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
                if ((Boolean)v.getTag()){

                    // if tag = true -> Favorite item click
                    addFoodToFavorite();
                    removeFoodFromFavorite();
                }
            }
        });
        holder.setListener(new FoodDetailOrCardClickListener() {
            @Override
            public void onFoodItemClickListener(View view, int position, boolean isDetail) {
                if (isDetail){
                    Toast.makeText(context, "Detail Click", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Cart Click", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void removeFoodFromFavorite() {
        StringRequest request = new StringRequest(Request.Method.DELETE, APIEndPoints.DELETE_FOOD_FROM_RESTAURANT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: " + response);
                try {

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

    private void addFoodToFavorite() {
        StringRequest request = new StringRequest(Request.Method.POST, APIEndPoints.POST_ADD_TO_FAV, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+ response);
                try {

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("key", "1234");
                params.put("foodId", "1234");
                params.put("restaurantId", "1234");
                params.put("restaurantName", "1234");
                params.put("foodName", "1234");
                params.put("foodImage", "1234");
                params.put("price", "1234");
                params.put("fbid", "1234");
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


}
