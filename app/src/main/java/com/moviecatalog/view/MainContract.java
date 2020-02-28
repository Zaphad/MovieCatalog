package com.moviecatalog.view;

import com.moviecatalog.model.Movie;

import java.util.List;

public interface MainContract {

    interface Model {

        interface OnFinishedListener {
            void onFinished(List<Movie> movieList, boolean isRefresh);

            void onFailure(Throwable t);

        }

        void getMovieList(OnFinishedListener onFinishedListener, int pageNumber);

        void updateMovieList(OnFinishedListener onFinishedListener, int pageNumber);

    }

    interface View {

        void showProgress();

        void hideProgress();

        void removeDataFromRecyclerView(List<Movie> movieArrayList);

        void setDataToRecyclerView(List<Movie> movieArrayList);

        void onResponseFailure(Throwable throwable);

    }

    interface Presenter {

        void onDestroy();

        void getMoreData(int pageNumber);

        void updateData(int pageNumber);

        void requestDataFromServer();

    }

}
