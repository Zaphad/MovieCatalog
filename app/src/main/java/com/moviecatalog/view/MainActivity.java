package com.moviecatalog.view;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.moviecatalog.R;
import com.moviecatalog.adapter.MoviesAdapter;
import com.moviecatalog.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieItemClickListener, MainContract.View {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;
    private MoviesAdapter moviesAdapter;
    private List<Movie> movieList;
    private MainPresenter mainPresenter;
    //setting refresh layout
    private SwipeRefreshLayout swipeContainer;

    private int pageNumber = 1;

    //pagination variables
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 20;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUserInterface();

        setListeners();

        mainPresenter = new MainPresenter(this);
        mainPresenter.requestDataFromServer();

    }

/*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        List<? extends Parcelable> parcelableMovieList = new ArrayList<>();
        parcelableMovieList.add(moviesAdapter.getMovieList().get(0));
        outState.putParcelableArray();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }
*/

    private void initializeUserInterface(){

        swipeContainer = findViewById(R.id.swipe_container);
        movieList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        moviesAdapter = new MoviesAdapter(movieList, this);
        recyclerView.setAdapter(moviesAdapter);

    }

    private void setListeners(){

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeContainer.setRefreshing(true);

                mainPresenter.updateData(pageNumber);
                swipeContainer.setRefreshing(false);

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (dy > 0) {
                    if (isLoading) {
                        if (totalItemCount > previousTotal) {
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItems + viewThreshold)) {

                        mainPresenter.getMoreData(pageNumber);
                        isLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public void onMovieItemClick(int position) {

        if (position == -1) {
            return;
        }

        Toast.makeText(this, movieList.get(position).getTitle(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showProgress() {

        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideProgress() {

        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void removeDataFromRecyclerView(List<Movie> movieArrayList) {

        moviesAdapter.removeMovieList(movieArrayList);

    }

    @Override
    public void setDataToRecyclerView(List<Movie> movieArrayList) {

        moviesAdapter.addMovies(movieArrayList);
        pageNumber++;

    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Snackbar.make(recyclerView,"Проверьте соединение с интернетом и попробуйте еще раз",Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.onDestroy();
    }
}
