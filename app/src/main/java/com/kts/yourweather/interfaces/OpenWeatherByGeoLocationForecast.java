package com.kts.yourweather.interfaces;

import com.kts.yourweather.model.forecast.WeatherForecastRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherByGeoLocationForecast {
    @GET("data/2.5/forecast")
    Call<WeatherForecastRequest> loadWeather(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String keyApi);
}
