package com.example.android.inagiffy.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GiphyResponse {

    @SerializedName("data")
    public List<Gif> trendingResults;

    public List<Gif> getTrendingResults() {
        return trendingResults;
    }

    public void setTrendingResults(List<Gif> trendingResults) {
        this.trendingResults = trendingResults;
    }

}
