package com.unofficialcoder.myrestaurantapp.model.eventBus;


import com.unofficialcoder.myrestaurantapp.model.FoodBean;

public class FoodDetailEvent {
    private boolean success;
    private FoodBean food;

    public FoodDetailEvent(boolean success, FoodBean food) {
        this.success = success;
        this.food = food;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public FoodBean getFood() {
        return food;
    }

    public void setFood(FoodBean food) {
        this.food = food;
    }
}
