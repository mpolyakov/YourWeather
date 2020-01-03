package com.kts.yourweather.interfaces;

import com.kts.yourweather.model.WeatherRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherByGeoLocation {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String keyApi);
}
