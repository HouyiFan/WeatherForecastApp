package com.example.houyifan.weatherforecast.data;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.houyifan.weatherforecast.controller.AppController;
import com.example.houyifan.weatherforecast.model.Forecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ForecastData {
    ArrayList<Forecast> forecastArrayList = new ArrayList<>();

    String urlLeft = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22";
    String urlRight = "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

//    String url = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    public void getForecast( final ForecastListAsyncResponse callback, String locationText){
        String url = urlLeft + locationText + urlRight;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d("Response: ", response.toString());
//                String city, country, region;

                try {
                    JSONObject query = response.getJSONObject("query");
                    JSONObject results = query.getJSONObject("results");
                    JSONObject channel = results.getJSONObject("channel");

                    JSONObject location = channel.getJSONObject("location");
//                    city = location.getString("city");
//                    country = location.getString("location");
//                    region = location.getString("region");


                    //Item Object
                    JSONObject itemObject = channel.getJSONObject("item");
                    String pubDate = itemObject.getString("pubDate");

                    //Condition Object
                    JSONObject conditionObject = itemObject.getJSONObject("condition");
//                    forecast.setDate(conditionObject.getString("date"));
//                    forecast.setCurrentTemperature(conditionObject.getString("temp"));
//                    forecast.setCurrentWeatherDescription(conditionObject.getString("text"));

                    //Forecast JsonArray
                    JSONArray forecastArray = itemObject.getJSONArray("forecast");
                    for (int i = 0; i < forecastArray.length(); i++){
                        JSONObject forecastObject = forecastArray.getJSONObject(i);

                        Forecast forecast = new Forecast();

                        forecast.setForecastDate(forecastObject.getString("date"));
                        forecast.setForecastDay(forecastObject.getString("day"));
                        forecast.setForecastLowTemp(forecastObject.getString("low"));
                        forecast.setForecastHighTemp(forecastObject.getString("high"));
                        forecast.setForecastWeatherDescription(forecastObject.getString("text"));

                        forecast.setDate(conditionObject.getString("date"));
                        forecast.setCurrentTemperature(conditionObject.getString("temp"));
                        forecast.setCurrentWeatherDescription(conditionObject.getString("text"));
                        forecast.setCity(location.getString("city"));
                        forecast.setRegion(location.getString("region"));
                        forecast.setDate(conditionObject.getString("date"));

                        forecast.setDescriptionHTML(itemObject.getString("description"));

                        forecastArrayList.add(forecast);
                    }
                } catch (JSONException e) {
//                    Toast.makeText(getApplicationContext, "Search returns no result", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } if(null != callback) { callback.processFinished(forecastArrayList);}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
