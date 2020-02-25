package com.moviecatalog.presenter;

import com.moviecatalog.model.Page;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IService {

    @GET("movies/")
    Call<Page> getPage(@Query("page") int pageIndex);

}
