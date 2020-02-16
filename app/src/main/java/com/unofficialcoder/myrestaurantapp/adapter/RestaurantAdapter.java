package com.unofficialcoder.myrestaurantapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.model.RestaurantBean;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.MyViewHolder> {

    Context context;
    List<RestaurantBean> restaurantBeanList;

    public RestaurantAdapter(Context context, List<RestaurantBean> restaurantBeanList) {
        this.context = context;
        this.restaurantBeanList = restaurantBeanList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_restaurant, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        RestaurantBean bean = restaurantBeanList.get(position);
        holder.restaurantName.setText(bean.getName());
        holder.restaurantAddress.setText(bean.getAddress());

        Picasso.get().load(R.drawable.mushroom_pizza).into(holder.coverImg);

    }

    @Override
    public int getItemCount() {
        return restaurantBeanList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantName, restaurantAddress;
        ImageView coverImg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurantName = itemView.findViewById(R.id.text_restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.text_restaurant_address);
            coverImg = itemView.findViewById(R.id.img_restaurant);
        }
    }
}
