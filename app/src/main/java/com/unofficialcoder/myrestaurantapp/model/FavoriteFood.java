package com.unofficialcoder.myrestaurantapp.model;

import java.util.List;

public class FavoriteFood {
    private boolean success;
    private List<FavoriteBean> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<FavoriteBean> getResult() {
        return result;
    }

    public void setResult(List<FavoriteBean> result) {
        this.result = result;
    }
}
