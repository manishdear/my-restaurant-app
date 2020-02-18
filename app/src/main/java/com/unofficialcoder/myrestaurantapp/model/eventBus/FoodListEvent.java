package com.unofficialcoder.myrestaurantapp.model.eventBus;

import com.unofficialcoder.myrestaurantapp.model.Category;
import com.unofficialcoder.myrestaurantapp.model.MenuBean;

public class FoodListEvent {

    private boolean success;

    private MenuBean category;

    public FoodListEvent(boolean success, MenuBean category) {
        this.success = success;
        this.category = category;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public MenuBean getCategory() {
        return category;
    }

    public void setCategory(MenuBean category) {
        this.category = category;
    }
}
