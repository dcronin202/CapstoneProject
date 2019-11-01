package com.example.android.inagiffy.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.inagiffy.R;
import com.example.android.inagiffy.data.Gif;
import com.example.android.inagiffy.data.GiphyApi;
import com.example.android.inagiffy.data.GiphyResponse;

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
    private MutableLiveData<List<Gif>> gifImages;


    public GifRepository(Application application) {
        apiKey = application.getString(R.string.api_key_giphy);
        gifImages = new MutableLiveData<>();
    }

    public LiveData<List<Gif>> getGifs() {
        return gifImages;
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

    public void callTrendingGifImages() {
        if (giphyApi == null) {
            getGifApi();
        }

        Call<GiphyResponse> call = giphyApi.getTrendingGifImages(apiKey);
        call.enqueue(new Callback<GiphyResponse>() {
            @Override
            public void onResponse(Call<GiphyResponse> call, Response<GiphyResponse> response) {
                onGifImageResponseReceived(response);
            }

            @Override
            public void onFailure(Call<GiphyResponse> call, Throwable t) {
                gifImages.postValue(new ArrayList<Gif>());
                Log.e(LOG, t.getMessage());
            }

        });

    }

    // Method for retrieving Search gifs

    public void callSearchGifImages(String searchText) {
        if (giphyApi == null) {
            getGifApi();
        }

        Call<GiphyResponse> call = giphyApi.getSearchGifImages(apiKey, searchText);
        call.enqueue(new Callback<GiphyResponse>() {
            @Override
            public void onResponse(Call<GiphyResponse> call, Response<GiphyResponse> response) {
                onGifImageResponseReceived(response);
            }

            @Override
            public void onFailure(Call<GiphyResponse> call, Throwable t) {
                gifImages.postValue(new ArrayList<Gif>());
                Log.e(LOG, t.getMessage());
            }
        });
    }

    // Method for receiving a gif response object and displaying its data

    private void onGifImageResponseReceived(Response<GiphyResponse> response) {

        if (response.isSuccessful()) {
            GiphyResponse giphyResponse = response.body();
            final List<Gif> gifList = giphyResponse.getGifImageResults();
            gifImages.postValue(gifList);

        } else {
            gifImages.postValue(new ArrayList<Gif>());
            Log.e(LOG, "Code: " + response.code());
        }
    }

}
