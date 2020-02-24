package com.moviecatalog.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.moviecatalog.R;
import com.moviecatalog.adapters.RecyclerViewAdapter;
import com.moviecatalog.model.Movie;
import com.moviecatalog.model.Page;
import com.moviecatalog.presenter.IPageApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.moviecatalog.presenter.RetrofitClient.getRetrofitClient;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;
    private IPageApi pageApi;
    private RecyclerViewAdapter recyclerViewAdapter;


    private int pageNumber = 1;

    //pag variables
    private boolean isLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount, previousTotal = 0;
    private int viewTreshold = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        pageApi = getRetrofitClient().create(IPageApi.class);

        progressBar.setVisibility(View.VISIBLE);

        Call<Page> call = pageApi.getPage(pageNumber);

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
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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

                    if (!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItems + viewTreshold)) {
                        pageNumber++;
                        pagination();

                        isLoading = true;

                    }

                }


            }
        });

    }

    private void pagination() {
        progressBar.setVisibility(View.VISIBLE);
        Call<Page> call = pageApi.getPage(pageNumber);

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
