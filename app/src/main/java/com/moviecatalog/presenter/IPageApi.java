package com.moviecatalog.presenter;

import com.moviecatalog.model.Movie;
import com.moviecatalog.model.Page;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IPageApi {

    @GET("movies/")
    Call<Page> getPage(@Query("page") int pageIndex);

}
