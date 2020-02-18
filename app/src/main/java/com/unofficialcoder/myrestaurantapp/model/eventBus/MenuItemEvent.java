package com.unofficialcoder.myrestaurantapp.model.eventBus;

import com.unofficialcoder.myrestaurantapp.model.RestaurantBean;

public class MenuItemEvent {

    private boolean success;
    private RestaurantBean restaurant;

    public MenuItemEvent(boolean success, RestaurantBean restaurant) {
        this.success = success;
        this.restaurant = restaurant;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public RestaurantBean getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantBean restaurant) {
        this.restaurant = restaurant;
    }
}
