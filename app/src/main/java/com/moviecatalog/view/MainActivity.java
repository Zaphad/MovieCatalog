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

import com.moviecatalog.R;
import com.moviecatalog.adapter.MoviesAdapter;
import com.moviecatalog.model.Movie;
import com.moviecatalog.model.Page;
import com.moviecatalog.network.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.moviecatalog.network.ApiClient.getApiClient;

public class MainActivity extends AppCompatActivity implements MovieItemClickListener{

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;
    private ApiInterface service;
    private MoviesAdapter moviesAdapter;
    private List<Movie> movieList;
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

        //setup swiperefreshlayout
        swipeContainer = findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeContainer.setRefreshing(true);
                refreshAll();

            }
        });

        movieList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        moviesAdapter = new MoviesAdapter(movieList, this);
        recyclerView.setAdapter(moviesAdapter);

        /*
        recyclerView.getChildCount();
        View View = recyclerView.getChildAt(0);
        */

        service = getApiClient().create(ApiInterface.class);

        progressBar.setVisibility(View.VISIBLE);

        Call<Page> call = service.getPage(pageNumber);

        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {

                List<Movie> movieList = response.body().getMovies();

                moviesAdapter.addMovies(movieList);
                Toast.makeText(MainActivity.this, "somthing happens", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
            }
        });

/*
        CardView cardView = findViewById(R.id.movie_card);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                Toast.makeText(MainActivity.this, "Card clicked", Toast.LENGTH_SHORT).show();

            }
        });*/


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
                        pageNumber++;
                        pagination();
                        isLoading = true;
                    }
                }
            }
        });
    }


    private void refreshAll(){

        //for (int i = 1; i<=pageNumber;i++) {

            Call<Page> call = service.getPage(1);

            call.enqueue(new Callback<Page>() {
                @Override
                public void onResponse(Call<Page> call, Response<Page> response) {

                    List<Movie> movieList = response.body().getMovies();
                    moviesAdapter.removeMovieList(movieList);
                    moviesAdapter.addMovies(movieList);
                    Toast.makeText(MainActivity.this, "updating", Toast.LENGTH_SHORT).show();
                    pageNumber = 1;
                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<Page> call, Throwable t) {

                }
            });
        //}

    }

    private void pagination() {
        progressBar.setVisibility(View.VISIBLE);
        Call<Page> call = service.getPage(pageNumber);

        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {

                if (!response.body().getNext().equals(null)) {

                    List<Movie> movieList = response.body().getMovies();

                    moviesAdapter.addMovies(movieList);
                    Toast.makeText(MainActivity.this, "Page " + pageNumber + " is loaded", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(MainActivity.this, "No more pages for load", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {

            }
        });

    }

    @Override
    public void onMovieItemClick(int position) {

        if (position == -1) {
            return;
        }

        Toast.makeText(MainActivity.this, "id is:" + movieList.get(position).getId() + movieList.get(position).getTitle(), Toast.LENGTH_SHORT).show();

    }
}
