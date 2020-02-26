package com.moviecatalog.view;

import com.moviecatalog.model.Movie;

import java.util.List;

public class MainPresenter implements MainContract.Presenter, MainContract.Model.OnFinishedListener{

    private MainContract.View movieListView;

    private MainContract.Model movieListModel;

    public MainPresenter(MainContract.View movieListView) {
        this.movieListView = movieListView;
        movieListModel = new MainModel();
    }

    @Override
    public void onDestroy() {
        this.movieListView = null;
    }

    @Override
    public void getMoreData(int pageNo) {
        if (movieListView != null) {
            movieListView.showProgress();
        }
        movieListModel.getMovieList(this, pageNo);
    }

    @Override
    public void updateData(int pageNo) {
        if (movieListView != null) {
            movieListView.showProgress();
        }
        movieListModel.updateMovieList(this, pageNo);
    }

    @Override
    public void requestDataFromServer() {

        if (movieListView != null) {
            movieListView.showProgress();
        }
        movieListModel.getMovieList(this, 1);

    }

    @Override
    public void onFinished(List<Movie> movieList, boolean isRefresh) {

        if(isRefresh){
            movieListView.removeDataFromRecyclerView(movieList);
        }
        else{
            movieListView.setDataToRecyclerView(movieList);
        }
        if (movieListView != null) {
            movieListView.hideProgress();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        movieListView.onResponseFailure(t);
        if (movieListView != null) {
            movieListView.hideProgress();
        }
    }
}
