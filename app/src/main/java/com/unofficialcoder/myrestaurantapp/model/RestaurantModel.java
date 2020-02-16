package com.unofficialcoder.myrestaurantapp.model;

import java.util.List;

public class RestaurantModel {
    private boolean success;
    private String message;
    private List<RestaurantBean> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RestaurantBean> getResult() {
        return result;
    }

    public void setResult(List<RestaurantBean> result) {
        this.result = result;
    }
}
