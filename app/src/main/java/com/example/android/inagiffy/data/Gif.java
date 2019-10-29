package com.example.android.inagiffy.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gif {

    @SerializedName("id")
    @Expose
    private String gifId;

    @Expose
    private boolean isFavorite;

    public Gif() {
    }

    public Gif(String gifId, boolean isFavorite) {
        this.gifId = gifId;
        this.isFavorite = isFavorite;
    }

    // GETTERS
    public String getGifId() {
        return gifId;
    }

    // TODO: Add as strings to values directory
    public String getGifUrl() {
        String url1 = "https://media.giphy.com/media/";
        String url2 = "/giphy.gif";
        return url1 + gifId + url2;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    // SETTERS
    public void setGifId(String gifId) {
        this.gifId = gifId;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}
