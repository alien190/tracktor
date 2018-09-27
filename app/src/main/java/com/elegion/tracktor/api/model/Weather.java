package com.elegion.tracktor.api.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("weather")
    private List<WeatherItem> weather;

    @SerializedName("main")
    private Main main;


    public void setWeather(List<WeatherItem> weather) {
        this.weather = weather;
    }

    public List<WeatherItem> getWeather() {
        return weather;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Main getMain() {
        return main;
    }

    @Override
    public String toString() {
        return
                "Weather{" +
                        ",weather = '" + weather + '\'' +
                        ",main = '" + main + '\'' +
                        "}";
    }
}