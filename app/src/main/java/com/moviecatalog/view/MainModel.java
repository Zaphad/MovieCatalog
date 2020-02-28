package com.moviecatalog.view;

import com.moviecatalog.model.Movie;
import com.moviecatalog.model.Page;
import com.moviecatalog.network.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.moviecatalog.network.ApiClient.getApiClient;

public class MainModel implements MainContract.Model{

    @Override
    public void getMovieList(OnFinishedListener onFinishedListener, int pageNumber) {

        ApiInterface apiInterface = getApiClient().create(ApiInterface.class);


        Call<Page> call = apiInterface.getPage(pageNumber);

        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {

                if (response.body() == null|| response.body().equals(null)){
                    return;
                }
                List<Movie> movieList = response.body().getMovies();
                onFinishedListener.onFinished(movieList,false);
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                onFinishedListener.onFailure(t);
            }
        });

    }

    @Override
    public void updateMovieList(OnFinishedListener onFinishedListener, int pageNumber) {

        ApiInterface apiInterface = getApiClient().create(ApiInterface.class);


            Call<Page> call = apiInterface.getPage(pageNumber);

            call.enqueue(new Callback<Page>() {
                @Override
                public void onResponse(Call<Page> call, Response<Page> response) {

                    List<Movie> movieList = response.body().getMovies();

                    onFinishedListener.onFinished(movieList,true);

                }

                @Override
                public void onFailure(Call<Page> call, Throwable t) {
                    onFinishedListener.onFailure(t);
                }
            });


    }
}
