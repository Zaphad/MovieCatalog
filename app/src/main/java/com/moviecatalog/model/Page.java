package com.moviecatalog.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Page {



    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("next")
    @Expose
    private String next;
    @SerializedName("previous")
    @Expose
    private String previous;
    @SerializedName("results")
    @Expose
    private List<Movie> movies = null;

    private final int currentPageIndex = getCurrentPageIndex();

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setResults(List<Movie> movies) {
        this.movies = movies;
    }

    public int getCurrentPageIndex(){

        if(next == null||next.equals(null))
        {
            return 201;
        }
        if (previous == null||previous.equals(null))
        {
            return 1;
        }
        previous.split("=");
        return currentPageIndex;

    }

}
