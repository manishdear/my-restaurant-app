package com.unofficialcoder.myrestaurantapp.Retrofit;

import com.unofficialcoder.myrestaurantapp.model.AddonModel;
import com.unofficialcoder.myrestaurantapp.model.CreateOrderModel;
import com.unofficialcoder.myrestaurantapp.model.DefaultResponse;
import com.unofficialcoder.myrestaurantapp.model.FavoriteFood;
import com.unofficialcoder.myrestaurantapp.model.FavoriteOnlyIdModel;
import com.unofficialcoder.myrestaurantapp.model.Food;
import com.unofficialcoder.myrestaurantapp.model.MenuModel;
import com.unofficialcoder.myrestaurantapp.model.RestaurantBean;
import com.unofficialcoder.myrestaurantapp.model.Restaurants;
import com.unofficialcoder.myrestaurantapp.model.SizeModel;
import com.unofficialcoder.myrestaurantapp.model.UpdateOrderModel;
import com.unofficialcoder.myrestaurantapp.model.UpdateUserModel;
import com.unofficialcoder.myrestaurantapp.model.UserModel;
import com.unofficialcoder.myrestaurantapp.storage.db.CartItem;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyRestaurantAPI {

    @GET("restaurant")
    Observable<Restaurants> getRestaurant(@Query("key") String apiKey);

    @GET("user")
    Observable<UserModel> getUser(@Query("key") String apiKey,
                                  @Query("fbid") String userPhone);

    @POST("user")
    @FormUrlEncoded
    Observable<UpdateUserModel> updateUserInfo(@Field("key") String apiKey,
                                               @Field("fbid") String fbid,
                                               @Field("userPhone") String userPhone,
                                               @Field("userName") String userName,
                                               @Field("userAddress") String userAddress
                                               );

    @GET("menu")
    Observable<MenuModel> getCategories(@Query("key") String apiKey,
                                        @Query("restaurantId") int restaurantId);

    @GET("food")
    Observable<Food> getFoodById(@Query("key") String apiKey,
                                 @Query("menuId") int menuId);

    @GET("food/ById")
    Observable<Food> getFoodByFoodId(@Query("key") String apiKey,
                                 @Query("foodId") int foodId);

    @GET("food/searchfood")
    Observable<Food> getFoodByName(@Query("key") String apiKey,
                                 @Query("foodName") String name);

    @GET("favorite")
    Observable<FavoriteFood> getFavoriteFood(@Query("key") String apiKey,
                                             @Query("fbid") String fbid);

    @POST("favorite")
    @FormUrlEncoded
    Observable<DefaultResponse> addFoodToFav(@Field("key") String apiKey,
                                                  @Field("fbid") String fbid,
                                                  @Field("foodId") int foodId,
                                                  @Field("restaurantId") int restaurantId,
                                             @Field("restaurantName") String restaurantName,
                                             @Field("foodName") String foodName,
                                             @Field("foodImage") String foodImage,
                                             @Field("price") double price);

    @DELETE("favorite")
    Observable<DefaultResponse> removeFoodFromFav(@Query("key") String apiKey,
                                                  @Query("fbid") String fbid,
                                                  @Query("foodId") int foodId,
                                                  @Query("restaurantId") int restaurantId);

    @GET("favorite/ByRestaurant")
    Observable<FavoriteOnlyIdModel> getFavoriteByRestaurant(@Query("key") String apiKey,
                                                            @Query("fbid") String fbid,
                                                            @Query("restaurantId") int restaurantId);

    @GET("size")
    Observable<SizeModel> getSizeOfFood(@Query("key") String apiKey,
                                        @Query("foodId") int foodId);

    @GET("addon")
    Observable<AddonModel> getAddonOfFood(@Query("key") String apiKey,
                                          @Query("foodId") int foodId);

    @POST("order/create")
    @FormUrlEncoded
    Observable<CreateOrderModel> createOrder(@Field("key") String key,
                                             @Field("orderFBID") String orderFBID,
                                             @Field("orderPhone") String orderPhone,
                                             @Field("orderName") String orderName,
                                             @Field("orderAddress") String orderAddress,
                                             @Field("orderDate") String orderDate,
                                             @Field("restaurantId") int restaurantId,
                                             @Field("transactionId") String transactionId,
                                             @Field("cod") int cod,
                                             @Field("totalPrice") Double totalPrice,
                                             @Field("numOfItem") int numOfItem);

    @POST("orderDetail/update")
    @FormUrlEncoded
    Observable<UpdateOrderModel> updateOrder(@Field("key") String apiKey,
                                             @Field("orderId") String orderId,
                                             @Field("orderDetail") String orderDetail);
}
