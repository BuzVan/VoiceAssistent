package com.voiceassistent.forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {
    @GET("/current?access_key=b6a38d0a5084d1061a09776df760ac42")
    Call<Forecast> getCurrentWeather(@Query("query") String city);
}
