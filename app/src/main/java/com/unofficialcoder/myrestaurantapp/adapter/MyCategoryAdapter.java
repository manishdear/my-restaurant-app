package com.unofficialcoder.myrestaurantapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.unofficialcoder.myrestaurantapp.activity.FoodListActivity;
import com.unofficialcoder.myrestaurantapp.activity.MenuActivity;
import com.unofficialcoder.myrestaurantapp.model.eventBus.FoodListEvent;
import com.unofficialcoder.myrestaurantapp.model.eventBus.MenuItemEvent;
import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;
import com.unofficialcoder.myrestaurantapp.utils.MyUtils;
import com.unofficialcoder.myrestaurantapp.R;
import com.unofficialcoder.myrestaurantapp.common.Common;
import com.unofficialcoder.myrestaurantapp.model.MenuBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Unbinder;

public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder> {

    Context context;
    List<MenuBean> categoryList;

    public MyCategoryAdapter(Context context, List<MenuBean> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_category, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MenuBean bean = categoryList.get(position);

        Picasso.get()
                .load(APIEndPoints.DEMO_IMAGE_SERVER_URL + bean.getImage())
                .into(holder.imgCategory);

        holder.textCategory.setText(bean.getName());

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgCategory;
        TextView textCategory;

        Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.txt_category);
            imgCategory = itemView.findViewById(R.id.img_category);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventBus.getDefault().postSticky(new FoodListEvent(true, categoryList.get(getAdapterPosition())));
                    context.startActivity(new Intent(context, FoodListActivity.class));

                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (categoryList.size() == 1){
            return Common.DEFAULT_COLUMN_COUNT;
        }else {
            if (categoryList.size() %2 == 0){
                return Common.DEFAULT_COLUMN_COUNT;
            }else {
                return (position > 1 && position == categoryList.size() - 1) ? Common.FULL_WIDTH_COLUMN : Common.DEFAULT_COLUMN_COUNT;
            }
        }
    }
}
