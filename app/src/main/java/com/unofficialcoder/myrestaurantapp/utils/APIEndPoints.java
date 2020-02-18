package com.unofficialcoder.myrestaurantapp.utils;

public class APIEndPoints {
    public static final String DEMO_SERVER_URL = "https://myresproject.herokuapp.com/";
    public static final String DEMO_IMAGE_SERVER_URL = "https://myresproject.000webhostapp.com/assets/";
    public static final String GET_ALL_RESTAURANT = DEMO_SERVER_URL + "restaurant?key=1234";
    public static final String GET_MENU_BY_RESTAURANT_ID = DEMO_SERVER_URL + "menu?key=1234&restaurantId=";
    public static final String GET_FOOD_BY_MENU_ID = DEMO_SERVER_URL + "food?key=1234&menuId=";


}