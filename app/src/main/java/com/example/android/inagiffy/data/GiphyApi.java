package com.example.android.inagiffy.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GiphyApi {

    @GET("trending")
    Call<GiphyResponse> getTrendingGifImages(@Query("api_key") String apiKey);

    @GET("search")
    Call<GiphyResponse> getSearchGifImages(@Query("api_key") String apiKey, @Query("q") String searchText);

}
