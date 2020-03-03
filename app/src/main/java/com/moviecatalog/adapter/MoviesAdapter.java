package com.moviecatalog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.moviecatalog.R;
import com.moviecatalog.model.Movie;
import com.moviecatalog.view.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder>{

    private List<Movie> movieList;
    private List<Movie> movieListFiltered;
    private List<Movie> movieListOriginal;

    private MainActivity mainActivity;

    public MoviesAdapter(List<Movie> movieList, MainActivity mainActivity) {
        this.movieList = movieList;
        this.mainActivity = mainActivity;
    }

    public List<Movie> getMovieListOriginal() {
        return movieListOriginal;
    }

    public void setMovieListOriginal(List<Movie> movieListOriginal) {
        this.movieListOriginal = movieListOriginal;
    }

    public List<Movie> getMovieListFiltered() {
        return movieListFiltered;
    }

    public void setMovieListFiltered(List<Movie> movieListFiltered) {
        this.movieListFiltered = movieListFiltered;
        notifyDataSetChanged();
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
        notifyDataSetChanged();
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.onMovieItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void addMovies(List<Movie> movieList) {
        if (!this.movieList.contains(movieList)) {
            for (Movie movie : movieList) {

                this.movieList.add(movie);
            }
        }
        notifyDataSetChanged();
    }

    public void clearMovieList() {
        this.movieList.clear();
        notifyDataSetChanged();
    }

    public void backUpMovieList() {

        this.movieListOriginal = this.movieList;
        notifyDataSetChanged();
    }

    public void restoreOriginalMovieList() {

        this.movieList = this.movieListOriginal;
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
