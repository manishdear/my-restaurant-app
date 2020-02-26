package com.unofficialcoder.myrestaurantapp.common;

import com.unofficialcoder.myrestaurantapp.model.FavoriteOnlyId;
import com.unofficialcoder.myrestaurantapp.model.RestaurantBean;

import java.util.List;

public class Common {

    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static RestaurantBean currentRestaurant;
    public static List<FavoriteOnlyId> currentFavRestaurant;

    public static boolean checkFavorite(String id, List<FavoriteOnlyId> currentFavRestaurant) {

        boolean result = false;
        for (FavoriteOnlyId item: currentFavRestaurant) {
            if (item.getFoodId().equals(id)){
                result = true;
            }
        }
        return result;
    }
}
