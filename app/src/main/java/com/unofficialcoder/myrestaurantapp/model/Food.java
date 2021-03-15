package com.unofficialcoder.myrestaurantapp.model;

import java.util.List;

public class Food {
    private boolean success;
    private List<FoodBean> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<FoodBean> getResult() {
        return result;
    }

    public void setResult(List<FoodBean> result) {
        this.result = result;
    }
}
