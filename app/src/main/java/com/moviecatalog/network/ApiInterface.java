package com.moviecatalog.network;

import com.moviecatalog.model.Page;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("movies/")
    Call<Page> getPage(@Query("page") int pageIndex);

    @GET("movies/")
    Call<Page> getPage(@Query("page") int pageIndex, Callback<Page> callback);

}
