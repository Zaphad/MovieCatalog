package com.moviecatalog.view;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements MovieItemClickListener, MainContract.View, SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;
    private MoviesAdapter moviesAdapter;
    private MainPresenter mainPresenter;
    private SearchView searchView;

    //setting refresh layout
    private SwipeRefreshLayout swipeContainer;

    private int pageNumber = 1;
    private int prevPageNumber = 1;

    //pagination variables
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUserInterface();
        setListeners();
        mainPresenter = new MainPresenter(this);
        mainPresenter.requestDataFromServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                moviesAdapter.restoreOriginalMovieList();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            List<Movie> filtered = filter(newText);
            Log.println(Log.INFO,"size ="," " + moviesAdapter.getMovieListFiltered().size());
            if (filtered.size() != 0) {
                new CountDownTimer(1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        moviesAdapter.setMovieList(filtered);
                    }
                }.start();
            }
            return true;
        } else {
            return false;
        }
    }

    private List<Movie> filter(String query){
        List<Movie> movieListFiltered = new ArrayList<>();
        String charString = query.toString();
        if (charString.isEmpty()) {
            movieListFiltered = moviesAdapter.getMovieListOriginal();
        } else {
            for (Movie movie : moviesAdapter.getMovieListOriginal()) {
                if (movie.getTitle().toLowerCase().contains(charString.toLowerCase())
                        || movie.getTitle().toUpperCase().contains(charString.toUpperCase())) {
                    movieListFiltered.add(movie);
                }
            }
            return movieListFiltered;
        }
        return null;
    }

    private void initializeUserInterface() {

        swipeContainer = findViewById(R.id.swipe_container);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 2);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        moviesAdapter = new MoviesAdapter(new ArrayList<>(), this);
        moviesAdapter.setMovieListFiltered(new ArrayList<>());
        moviesAdapter.setMovieListOriginal(new ArrayList<>());
        recyclerView.setAdapter(moviesAdapter);
    }

    private void setListeners() {

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                mainPresenter.updateData(1);
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
                        Log.println(Log.INFO, "pg number: ", Integer.toString(pageNumber));
                        if (prevPageNumber != pageNumber) {
                            mainPresenter.getMoreData(pageNumber);
                            prevPageNumber = pageNumber;
                        }
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
        Toast.makeText(this, moviesAdapter.getMovieList().get(position).getTitle(), Toast.LENGTH_SHORT).show();
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
        moviesAdapter.backUpMovieList();
        moviesAdapter.clearMovieList();
        previousTotal = 0;
        pageNumber = 1;
        prevPageNumber = 1;
    }

    @Override
    public void setDataToRecyclerView(List<Movie> movieArrayList) {
        moviesAdapter.backUpMovieList();
        moviesAdapter.addMovies(movieArrayList);
        pageNumber++;
    }

    @Override
    public void onResponseFailure(Throwable throwable) {
        Snackbar.make(recyclerView, "Проверьте соединение с интернетом и попробуйте еще раз", Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.onDestroy();
    }
    
}
