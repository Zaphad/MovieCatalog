package com.moviecatalog.view;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.moviecatalog.R;
import com.moviecatalog.adapters.RecyclerViewAdapter;
import com.moviecatalog.model.Movie;
import com.moviecatalog.model.Page;
import com.moviecatalog.presenter.IService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.moviecatalog.presenter.RetrofitClient.getRetrofitClient;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;
    private IService service;
    private RecyclerViewAdapter recyclerViewAdapter;
    //setting refresh layout
    private SwipeRefreshLayout swipeContainer;


    private int pageNumber = 1;

    //pagination variables
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewThreshold = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup swiperefreshlayout
        swipeContainer = findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refreshPage();
                refreshAll();
            }
        });


        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        service = getRetrofitClient().create(IService.class);

        progressBar.setVisibility(View.VISIBLE);

        Call<Page> call = service.getPage(pageNumber);

        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {

                List<Movie> movieList = response.body().getMovies();

                recyclerViewAdapter = new RecyclerViewAdapter(movieList, MainActivity.this);
                recyclerView.setAdapter(recyclerViewAdapter);
                Toast.makeText(MainActivity.this, "somthing happens", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                Snackbar snackbar;
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
                        pageNumber++;
                        pagination();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void refreshAll(){

        pageNumber = 1;
        Call<Page> call = service.getPage(pageNumber);

        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {

                    List<Movie> movieList = response.body().getMovies();
                    recyclerViewAdapter = new RecyclerViewAdapter(movieList, MainActivity.this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    Toast.makeText(MainActivity.this, "updating", Toast.LENGTH_SHORT).show();
                    swipeContainer.setRefreshing(false);
            }
            @Override
            public void onFailure(Call<Page> call, Throwable t) {

            }
        });
    }

    private void pagination() {
        progressBar.setVisibility(View.VISIBLE);
        Call<Page> call = service.getPage(pageNumber);

        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {

                if (!response.body().getNext().equals(null)) {

                    List<Movie> movieList = response.body().getMovies();

                    recyclerViewAdapter.addMovies(movieList);
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

}
