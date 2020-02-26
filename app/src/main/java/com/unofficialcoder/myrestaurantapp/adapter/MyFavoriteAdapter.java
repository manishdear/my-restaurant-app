package com.unofficialcoder.myrestaurantapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.unofficialcoder.myrestaurantapp.MyApplication;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.activity.FavoriteActivity;
import com.unofficialcoder.myrestaurantapp.activity.FoodDetailsActivity;
import com.unofficialcoder.myrestaurantapp.activity.FoodListActivity;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.model.FavoriteBean;
import com.unofficialcoder.myrestaurantapp.model.FoodBean;
import com.unofficialcoder.myrestaurantapp.model.MenuBean;
import com.unofficialcoder.myrestaurantapp.model.eventBus.FoodListEvent;
import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;
import com.unofficialcoder.myrestaurantapp.utils.MyUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Unbinder;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.MyViewHolder> {
    private static final String TAG = "MyFavoriteAdapter";

    Context context;
    List<FavoriteBean> favoriteList;
    List<FoodBean> foodDetails;

    public MyFavoriteAdapter(Context context, List<FavoriteBean> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
        foodDetails = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_favorite_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FavoriteBean bean = favoriteList.get(position);

        Picasso.get()
                .load(APIEndPoints.DEMO_IMAGE_SERVER_URL + bean.getFoodImage() )
                .placeholder(R.drawable.app_icon)
                .into(holder.imgFood);

        holder.textFoodName.setText(bean.getFoodName());
        holder.textFoodPrice.setText("$" + bean.getPrice());
        holder.textRestaurantName.setText(bean.getRestaurantName());

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgFood;
        TextView textFoodName, textFoodPrice, textRestaurantName;

        Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textFoodName = itemView.findViewById(R.id.txt_food_name);
            textFoodPrice = itemView.findViewById(R.id.txt_food_price);
            textRestaurantName = itemView.findViewById(R.id.txt_restaurant_name);
            imgFood = itemView.findViewById(R.id.img_food);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getFoodDetails(favoriteList.get(getAdapterPosition()).getFoodId());

//                    EventBus.getDefault().postSticky(new FoodListEvent(true, categoryList.get(getAdapterPosition())));
//                    context.startActivity(new Intent(context, FoodListActivity.class));

                }
            });
        }
    }

    private void getFoodDetails(String foodId){
        StringRequest request = new StringRequest(Request.Method.GET, APIEndPoints.GET_FOOD_BY_ID+foodId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "getFoodDetails: "+ response);
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
                                foodDetails.add(bean);

                                context.startActivity(new Intent(context, FoodDetailsActivity.class));
                                //EventBus.getDefault().postSticky(new FoodDetailEvent(true, bean));
                            }
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
                MyUtils.showVolleyError(error, TAG, context);
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
