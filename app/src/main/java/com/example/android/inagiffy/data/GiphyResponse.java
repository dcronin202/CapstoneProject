package com.example.android.inagiffy.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GiphyResponse {

    @SerializedName("data")
    public List<Gif> gifImageResults;

    public List<Gif> getGifImageResults() {
        return gifImageResults;
    }

    public void setGifImageResults(List<Gif> gifImageResults) {
        this.gifImageResults = gifImageResults;
    }

}
