package com.unofficialcoder.myrestaurantapp.common;

import com.unofficialcoder.myrestaurantapp.model.Addon;
import com.unofficialcoder.myrestaurantapp.model.FavoriteOnlyId;
import com.unofficialcoder.myrestaurantapp.model.RestaurantBean;
import com.unofficialcoder.myrestaurantapp.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Common {

    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static final String API_RESTAURANT_ENDPOINT = "https://myresproject.herokuapp.com/";
    public static final String API_KEY = "1234";
    public static RestaurantBean currentRestaurant;
    public static List<FavoriteOnlyId> currentFavRestaurant;
    public static User currentUser;
    public static List<FavoriteOnlyId> currentFavOfRestaurant;

    public static Set<Addon> addonList = new HashSet<>();

    public static boolean checkFavorite(int id, List<FavoriteOnlyId> currentFavRestaurant) {

        boolean result = false;
        for (FavoriteOnlyId item: currentFavRestaurant) {
            if (item.getFoodId().equals(id)){
                result = true;
            }
        }
        return result;
    }

    public static boolean checkFavorite(int id) {
        boolean result = false;
        for (FavoriteOnlyId item : currentFavOfRestaurant) {
            if (!item.getFoodId().equals(id)) {
                continue;
            }
            result = true;
        }
        return result;
    }

    public static void removeFavorite(int id) {
        for (FavoriteOnlyId item : currentFavOfRestaurant) {
            if (item.getFoodId().equals(id)) {
                currentFavOfRestaurant.remove(item);
            }
        }
    }
}
