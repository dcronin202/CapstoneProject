package com.example.android.inagiffy.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favorites")
public class Gif {

    @NonNull
    @PrimaryKey //(autoGenerate = true)
    @SerializedName("id")
    @Expose
    private String gifId;

    @ColumnInfo(name = "is_favorite")
    @Expose
    private boolean isFavorite;

    @Ignore
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

    public boolean getIsFavorite() {
        return isFavorite;
    }

    // SETTERS
    public void setGifId(String gifId) {
        this.gifId = gifId;
    }

    public void setIsFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}
