package com.roxy.northernstar;


import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("autosuggest")
    Single<ResponseModel> getPersonData(@Query("app_id") String app_id , @Query("app_code") String app_code , @Query("at") String at , @Query("q") String q);
}