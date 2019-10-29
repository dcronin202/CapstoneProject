package com.example.android.inagiffy.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android.inagiffy.data.Gif;
import com.example.android.inagiffy.data.GiphyApi;
import com.example.android.inagiffy.data.TrendingResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GifRepository {

    private static final String LOG = GifRepository.class.getSimpleName();
    private static final String apiKey = "";
    private static final String TRENDING_URL = "https://api.giphy.com/v1/gifs/";
    private GiphyApi giphyApi;
    private MutableLiveData<List<Gif>> gifImages;


    public GifRepository(Application application) {
        gifImages = new MutableLiveData<>();
    }

    public LiveData<List<Gif>> getGifDetails() {
        return gifImages;
    }


    // RETROFIT Methods

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(TRENDING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private void getGifApi() {
        giphyApi = retrofit.create(GiphyApi.class);
    }

    public void callGifImages() {
        if (giphyApi == null) {
            getGifApi();
        }

        Call<TrendingResponse> call = giphyApi.getTrendingGifImages(apiKey);
        call.enqueue(new Callback<TrendingResponse>() {

            @Override
            public void onResponse(Call<TrendingResponse> call, Response<TrendingResponse> response) {
                onGifImageResponseReceived(response);
            }

            @Override
            public void onFailure(Call<TrendingResponse> call, Throwable t) {
                gifImages.postValue(new ArrayList<Gif>());
                Log.e(LOG, t.getMessage());
            }

        });

    }

    private void onGifImageResponseReceived(Response<TrendingResponse> response) {

        if (response.isSuccessful()) {
            TrendingResponse trendingResponse = response.body();
            final List<Gif> gifList = trendingResponse.getTrendingResults();
            gifImages.postValue(gifList);

        } else {
            gifImages.postValue(new ArrayList<Gif>());
            Log.e(LOG, "Code: " + response.code());

        }

    }

}
