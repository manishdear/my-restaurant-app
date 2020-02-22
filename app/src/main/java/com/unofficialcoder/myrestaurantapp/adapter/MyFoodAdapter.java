package com.unofficialcoder.myrestaurantapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.activity.FoodListActivity;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.interfaces.FoodDetailOrCardClickListener;
import com.unofficialcoder.myrestaurantapp.model.FoodBean;
import com.unofficialcoder.myrestaurantapp.model.MenuBean;
import com.unofficialcoder.myrestaurantapp.model.eventBus.FoodListEvent;
import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Unbinder;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyViewHolder> {

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
                .inflate(R.layout.layout_food, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FoodBean bean = foodList.get(position);

        Picasso.get().load(APIEndPoints.DEMO_IMAGE_SERVER_URL + bean.getImage())
                .into(holder.img_food);

//        Glide.with(context).load(APIEndPoints.DEMO_IMAGE_SERVER_URL + bean.getImage())
//                .placeholder(R.drawable.menu_chinese)
//                .into(holder.img_food);

        holder.text_food_name.setText(bean.getName());
        holder.text_food_price.setText("$" + bean.getPrice());

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

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void onStop() {
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_food, img_detail, img_cart;
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
