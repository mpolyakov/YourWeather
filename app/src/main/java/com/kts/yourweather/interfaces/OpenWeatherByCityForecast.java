package com.kts.yourweather.interfaces;

import com.kts.yourweather.model.forecast.WeatherForecastRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherByCityForecast {
    @GET("data/2.5/forecast")
    Call<WeatherForecastRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);
}
