package com.unofficialcoder.myrestaurantapp.utils;

public class APIEndPoints {
    public static final String DEMO_SERVER_URL = "https://myresproject.herokuapp.com/";
    public static final String DEMO_IMAGE_SERVER_URL = "https://myresproject.000webhostapp.com/assets/";
    public static final String GET_ALL_RESTAURANT = DEMO_SERVER_URL + "restaurant?key=1234";
    public static final String GET_MENU_BY_RESTAURANT_ID = DEMO_SERVER_URL + "menu?key=1234&restaurantId=";
    public static final String GET_FOOD_BY_MENU_ID = DEMO_SERVER_URL + "food?key=1234&menuId=";
    public static final String SEARCH_FOOD = DEMO_SERVER_URL + "food/searchfood?key=1234&foodName=";
    public static final String GET_FAVORITE_FOOD = DEMO_SERVER_URL + "favorite?key=1234&fbid=2739799736047038";
    public static final String GET_FOOD_BY_ID = DEMO_SERVER_URL + "food/ById?key=1234&foodId=";


}