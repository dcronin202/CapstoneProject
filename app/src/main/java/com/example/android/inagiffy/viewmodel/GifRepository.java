package com.example.android.inagiffy.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.android.inagiffy.R;
import com.example.android.inagiffy.data.Gif;
import com.example.android.inagiffy.data.GiphyApi;
import com.example.android.inagiffy.data.GiphyResponse;
import com.example.android.inagiffy.database.GifDao;
import com.example.android.inagiffy.database.GifFavoritesDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GifRepository {

    private static final String LOG = GifRepository.class.getSimpleName();
    private static final String baseGiphyUrl = "https://api.giphy.com/v1/gifs/";
    private String apiKey = "";
    private GiphyApi giphyApi;
    private GifDao gifDao;
    private MutableLiveData<List<Gif>> gifImages;


    public GifRepository(Application application) {
        apiKey = application.getString(R.string.api_key_giphy);
        GifFavoritesDatabase database = GifFavoritesDatabase.getInstance(application);
        gifDao = database.gifDao();
        gifImages = new MutableLiveData<>();
    }

    public LiveData<List<Gif>> getGifs() {
        return gifImages;
    }

    // TODO: Fix this from sending favorites to Favorite screen
    public void getFavoritesList(LifecycleOwner owner) {
        gifDao.loadAllGifImages().observe(owner, new Observer<List<Gif>>() {
            @Override
            public void onChanged(List<Gif> databaseGifs) {
                if (databaseGifs != null) {
                    gifImages.postValue(databaseGifs);
                }
            }
        });
    }

    // RETROFIT Methods

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseGiphyUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private void getGifApi() {
        giphyApi = retrofit.create(GiphyApi.class);
    }

    // Method for retrieving Trending gifs

    public void callTrendingGifImages(final LifecycleOwner owner) {
        if (giphyApi == null) {
            getGifApi();
        }

        Call<GiphyResponse> call = giphyApi.getTrendingGifImages(apiKey);
        call.enqueue(new Callback<GiphyResponse>() {
            @Override
            public void onResponse(Call<GiphyResponse> call, Response<GiphyResponse> response) {
                onGifImageResponseReceived(response, owner);
            }

            @Override
            public void onFailure(Call<GiphyResponse> call, Throwable t) {
                gifImages.postValue(new ArrayList<Gif>());
                Log.e(LOG, t.getMessage());
            }

        });

    }

    // Method for retrieving Search gifs

    public void callSearchGifImages(String searchText, final LifecycleOwner owner) {
        if (giphyApi == null) {
            getGifApi();
        }

        Call<GiphyResponse> call = giphyApi.getSearchGifImages(apiKey, searchText);
        call.enqueue(new Callback<GiphyResponse>() {
            @Override
            public void onResponse(Call<GiphyResponse> call, Response<GiphyResponse> response) {
                onGifImageResponseReceived(response, owner);
            }

            @Override
            public void onFailure(Call<GiphyResponse> call, Throwable t) {
                gifImages.postValue(new ArrayList<Gif>());
                Log.e(LOG, t.getMessage());
            }
        });
    }

    // Method for receiving a gif response object and displaying its data

    private void onGifImageResponseReceived(Response<GiphyResponse> response, LifecycleOwner owner) {

        if (response.isSuccessful()) {
            final GiphyResponse giphyResponse = response.body();
            final List<Gif> gifList = giphyResponse.getGifImageResults();
            // TODO: Not working
            gifDao.loadAllGifImages().observe(owner, new Observer<List<Gif>>() {
                @Override
                public void onChanged(List<Gif> databaseGifs) {
                    if (databaseGifs != null) {
                        // Loop through the gif network to find the matching one in the DB
                        for (int indexForNetworkResults = 0; indexForNetworkResults < gifList.size(); indexForNetworkResults++) {
                            // Loop through the DB movies to see if its the same one
                            Gif gifFromNetwork = gifList.get(indexForNetworkResults);
                            for (int indexForDbResult = 0; indexForDbResult < databaseGifs.size(); indexForDbResult++) {
                                Gif gifFromDb = databaseGifs.get(indexForDbResult);
                                if (gifFromDb.getGifId().equals(gifFromNetwork.getGifId())) {
                                    gifFromNetwork.setIsFavorite(true);
                                }
                            }
                        }
                    }
                }
            });
            gifImages.postValue(gifList);

        } else {
            gifImages.postValue(new ArrayList<Gif>());
            Log.e(LOG, "Code: " + response.code());
        }
    }

    // AsyncTasks to Insert and Remove favorite GIF images
    private class InsertFavoritesAsyncTask extends AsyncTask<Gif, Void, List<Gif>> {

        @Override
        protected List<Gif> doInBackground(Gif... gifs) {
            gifDao.insertGif(gifs[0]);
            return null;
        }
    }

    private class RemoveFavoritesAsyncTask extends AsyncTask<Gif, Void, List<Gif>> {

        @Override
        protected List<Gif> doInBackground(Gif... gifs) {
            gifDao.removeGif(gifs[0]);
            return null;
        }
    }

    // Insert and Remove favorite GIF images
    public void insertGif(Gif gif) {
        new InsertFavoritesAsyncTask().execute(gif);
    }

    public void removeGif(Gif gif) {
        new RemoveFavoritesAsyncTask().execute(gif);
    }

}
