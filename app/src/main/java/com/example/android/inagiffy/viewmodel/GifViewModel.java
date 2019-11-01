package com.example.android.inagiffy.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.inagiffy.data.Gif;

import java.util.List;

public class GifViewModel extends AndroidViewModel {

    private static final String LOG = GifViewModel.class.getSimpleName();
    private GifRepository gifRepository;
    private Gif gifDetails;


    public GifViewModel(@NonNull Application application) {
        super(application);
        gifRepository = new GifRepository(application);
        Log.d(LOG, "Actively retrieving gifs from database");
    }

    public LiveData<List<Gif>> getGifImages() {
        return gifRepository.getGifs();
    }

    public Gif getGifDetails() {
        return gifDetails;
    }

    public void setGifDetails(Gif gif) {
        this.gifDetails = gif;
    }

    // Favorites
    public void addFavorite(Gif gif) {
        gif.setIsFavorite(true);
    }

    public void removeFavorite(Gif gif) {
    }

    // Trending
    public void getTrendingGifList() {
        gifRepository.callTrendingGifImages();
    }

    // Search
    public void getSearchGifList(String query) {
        gifRepository.callSearchGifImages(query);
    }

}
