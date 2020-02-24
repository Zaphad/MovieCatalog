package com.moviecatalog.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.moviecatalog.R;
import com.moviecatalog.adapters.RecyclerViewAdapter;
import com.moviecatalog.model.Movie;
import com.moviecatalog.model.Page;
import com.moviecatalog.presenter.IPageApi;
import com.moviecatalog.presenter.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.moviecatalog.presenter.RetrofitClient.getRetrofitClient;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private GridLayoutManager layoutManager;
    private IPageApi pageApi;
    private RecyclerViewAdapter recyclerViewAdapter;


    private int pageNumber = 1;

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

                recyclerViewAdapter = new RecyclerViewAdapter(movieList,MainActivity.this);
                recyclerView.setAdapter(recyclerViewAdapter);
                Toast.makeText(MainActivity.this, "somthing happens",Toast.LENGTH_SHORT).show();
                /*
                int i = 0;
                for (ImageView imageView : imageViewList) {
                    Picasso.get().load(movieList.get(i).getPosterImage()).into(imageView);
                    i++;
                }*/
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {

            }
        });

        /*
        retrofit = getRetrofitClient();

        pageApi = retrofit.create(IPageApi.class);
        */
        /*
        List<ImageView> imageViewList = new ArrayList<>(20);

        Resources r = getResources();
        String name = getPackageName();
        int[] ids = new int[20];

        for (int i = 1; i < ids.length + 1; i++) {
            ids[i - 1] = r.getIdentifier("image" + i, "id", name);
        }
        for (int i = 0; i < 20; i++) {
            imageViewList.add(findViewById(ids[i]));
        }*/




    }
}
