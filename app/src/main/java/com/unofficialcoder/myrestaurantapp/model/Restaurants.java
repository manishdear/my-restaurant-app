package com.unofficialcoder.myrestaurantapp.model;

import java.util.List;

public class Restaurants {
    private boolean success;
    private List<RestaurantBean> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<RestaurantBean> getResult() {
        return result;
    }

    public void setResult(List<RestaurantBean> result) {
        this.result = result;
    }
}
