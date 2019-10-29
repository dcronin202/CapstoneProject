package com.example.android.inagiffy.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GiphyApi {

    @GET("trending")
    Call<TrendingResponse> getTrendingGifImages(@Query("api_key") String apiKey);

}
