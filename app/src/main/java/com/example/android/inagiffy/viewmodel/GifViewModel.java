package com.example.android.inagiffy.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.android.inagiffy.data.Gif;

import java.util.List;

public class GifViewModel extends AndroidViewModel {

    private static final String LOG = GifViewModel.class.getSimpleName();
    private GifRepository gifRepository;

    private static final String viewState = "view_state";
    public static final String trendingView = "trending";
    public static final String favoritesView = "favorites";
    private String currentView = "";
    private SharedPreferences mPreferences;


    public GifViewModel(@NonNull Application application) {
        super(application);
        gifRepository = new GifRepository(application);
        Log.d(LOG, "Actively retrieving gifs from database");
    }

    public LiveData<List<Gif>> getGifImages() {
        return gifRepository.getGifs();
    }


    // Favorites
    public void getFavorites(LifecycleOwner owner) {
        gifRepository.getFavoritesList(owner);
        saveViewState(favoritesView);

    }

    public void addFavorite(final String gifId) {
        gifRepository.getGifs().observeForever(new Observer<List<Gif>>() {
            @Override
            public void onChanged(List<Gif> gifList) {
                for (int indexForNetworkResults = 0; indexForNetworkResults < gifList.size(); indexForNetworkResults++) {
                    Gif gifFromNetwork = gifList.get(indexForNetworkResults);
                    if (gifFromNetwork.getGifId().equals(gifId)) {
                        gifFromNetwork.setIsFavorite(true);
                        gifRepository.insertGif(gifFromNetwork);
                    }
                }
                gifRepository.getGifs().removeObserver(this);
            }
        });
    }

    public void removeFavorite(final String gifId) {
        gifRepository.getGifs().observeForever(new Observer<List<Gif>>() {
            @Override
            public void onChanged(List<Gif> gifList) {
                for (int indexForNetworkResults = 0; indexForNetworkResults < gifList.size(); indexForNetworkResults++) {
                    Gif gifFromNetwork = gifList.get(indexForNetworkResults);
                    if (gifFromNetwork.getGifId().equals(gifId)) {
                        gifFromNetwork.setIsFavorite(false);
                        gifRepository.removeGif(gifFromNetwork);
                    }
                }
                gifRepository.getGifs().removeObserver(this);
            }
        });
    }

    // View State
    private void saveViewState(String newState) {
        currentView = newState;
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(viewState, currentView);
        editor.commit();
    }

    public void setupSharedPref(SharedPreferences sharedPreferences) {
        if (mPreferences == null) {
            this.mPreferences = sharedPreferences;
        }
    }

    public void loadGifImages(LifecycleOwner owner) {
        String showViewState = mPreferences.getString(viewState, "default");
        if (showViewState.equals(favoritesView)) {
            getFavorites(owner);
        } else {
            getTrendingGifList(owner);
        }
    }

    // Trending
    public void getTrendingGifList(LifecycleOwner owner) {
        gifRepository.callTrendingGifImages(owner);
        saveViewState(trendingView);
    }

    // Search
    public void getSearchGifList(String query, LifecycleOwner owner) {
        gifRepository.callSearchGifImages(query, owner);
    }

}
