package com.unofficialcoder.myrestaurantapp.Retrofit;

import com.unofficialcoder.myrestaurantapp.model.UpdateUserModel;
import com.unofficialcoder.myrestaurantapp.model.UserModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IMyRestaurantAPI {

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
}
