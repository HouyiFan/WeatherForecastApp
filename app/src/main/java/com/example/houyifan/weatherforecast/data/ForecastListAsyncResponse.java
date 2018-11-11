package com.example.houyifan.weatherforecast.data;

import com.example.houyifan.weatherforecast.model.Forecast;

import java.util.ArrayList;

public interface ForecastListAsyncResponse {
    void processFinished(ArrayList<Forecast> forecastArrayList);
}
