package com.moviecatalog.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.moviecatalog.R;
import com.moviecatalog.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Movie> movieList;

    private Context context;

    public RecyclerViewAdapter(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public Context getContext() {
        return context;
    }

    public void setMovieList(List<Movie> imageList) {
        this.movieList = movieList;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Movie movie = movieList.get(position);
        holder.titles.setText(movie.getTitle());
        Picasso.get().load(movie.getPosterImage()).into(holder.posters);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void addMovies(List<Movie> movieList) {

        for (Movie movie : movieList) {

            this.movieList.add(movie);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView posters;
        TextView titles;

        public ViewHolder(View itemView) {
            super(itemView);
            posters = itemView.findViewById(R.id.movie_poster);
            titles = itemView.findViewById(R.id.movie_title);
        }
    }

}
