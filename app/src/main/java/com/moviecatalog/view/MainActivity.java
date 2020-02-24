package com.moviecatalog.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;

import com.moviecatalog.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<ImageView> imageViewList = new ArrayList<>(20);

        Resources r = getResources();
        String name = getPackageName();
        int[] ids = new int[20];

        for (int i = 1; i < ids.length + 1; i++) {
            ids[i - 1] = r.getIdentifier("image" + i, "id", name);
        }
        for (int i = 0; i < 20; i++) {
            imageViewList.add(findViewById(ids[i]));
        }
        Retrofit retrofit = getRetrofitClient();

        IPageApi pageApi = retrofit.create(IPageApi.class);

        Call<Page> call = pageApi.getPage(1);

        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {

                List<Movie> movieList = response.body().getMovies();

                int i = 0;
                for (ImageView imageView : imageViewList) {
                    Picasso.get().load(movieList.get(i).getPosterImage()).into(imageView);
                    i++;
                }

            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {

            }
        });

    }
}
