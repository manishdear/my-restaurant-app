package com.unofficialcoder.myrestaurantapp.model;

import java.util.List;

public class MenuModel {

    private boolean success;
    private String message;
    private List<MenuBean> result;

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

    public List<MenuBean> getResult() {
        return result;
    }

    public void setResult(List<MenuBean> result) {
        this.result = result;
    }
}
