package com.elegion.tracktor.api;

import com.elegion.tracktor.BuildConfig;
import com.elegion.tracktor.api.model.Weather;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenweathermapApi {
    @GET("data/2.5/weather?lang=ru&units=metric&APPID="+BuildConfig.API_KEY)
    Single<Weather> getWeather(@Query("lat") double lat, @Query("lon") double lon);
}
