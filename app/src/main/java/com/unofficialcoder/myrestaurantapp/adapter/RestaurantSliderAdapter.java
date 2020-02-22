package com.unofficialcoder.myrestaurantapp.adapter;

import android.content.Context;

import com.unofficialcoder.myrestaurantapp.utils.APIEndPoints;
import com.unofficialcoder.myrestaurantapp.model.RestaurantBean;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class RestaurantSliderAdapter extends SliderAdapter {

    List<RestaurantBean> restaurantBeanList;
    Context context;

    public RestaurantSliderAdapter(List<RestaurantBean> restaurantBeanList, Context context) {
        this.restaurantBeanList = restaurantBeanList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return restaurantBeanList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(APIEndPoints.DEMO_IMAGE_SERVER_URL + restaurantBeanList.get(position).getImage());
    }
}
