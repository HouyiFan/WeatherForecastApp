package com.example.houyifan.weatherforecast;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.houyifan.weatherforecast.data.ForecastData;
import com.example.houyifan.weatherforecast.data.ForecastListAsyncResponse;
import com.example.houyifan.weatherforecast.data.ForecastViewPagerAdapter;
import com.example.houyifan.weatherforecast.model.Forecast;
import com.example.houyifan.weatherforecast.util.Prefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ForecastViewPagerAdapter forecastViewPagerAdapter;
    private ViewPager viewPager;
    private TextView locationText;
    private TextView currentTempText;
    private  TextView currentDate;
    private EditText userLocationText;
    private String userEnteredString;
    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationText = findViewById(R.id.locationTextViewId);
        currentTempText = findViewById(R.id.currentTempId);
        currentDate = findViewById(R.id.currentDateId);
        userLocationText = findViewById(R.id.locationNameId);

        icon = findViewById(R.id.weatherIcon);

        final Prefs prefs = new Prefs(this);
        String prefsLocation = prefs.getLocation();
        getWeather(prefsLocation);

        userLocationText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {

                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                    && (keyCode == KeyEvent.KEYCODE_ENTER)){
//                    Toast.makeText(getApplicationContext(), userLocationText.getText().toString(), Toast.LENGTH_LONG).show();

                    userEnteredString = userLocationText.getText().toString();
                    prefs.setLocation(userEnteredString);
                    getWeather(userEnteredString);

                    return true;
                }
                return false;
            }
        });

//        String location = userLocationText.getText().toString().trim();

//        forecastViewPagerAdapter = new ForecastViewPagerAdapter(getSupportFragmentManager(), getFragments());

//        viewPager = findViewById(R.id.viewPager);
//        viewPager.setAdapter(forecastViewPagerAdapter);

//        ForecastData forecastData = new ForecastData();
//        forecastData.getForecast(new ForecastListAsyncResponse() {
//            @Override
//            public void processFinished(ArrayList<Forecast> forecastArrayList) {
//                for(int i = 0; i < forecastArrayList.size(); i++){
//                    Log.d("Forecast ", forecastArrayList.get(i).getForecastHighTemp());
//                }
//            }
//        });
    }

    private String getImageUrl(String html){

        String imgRegex = "(?i)<img[^>]+?src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
//        String htmlString = "<![CDATA[<img src=\\\"http://l.yimg.com/a/i/us/we/52/26.gif\\\"/>\\n<BR />\\n<b>Current Conditions:</b>\\n<BR />Cloudy\\n<BR />\\n<BR />\\n<b>Forecast:</b>\\n<BR />";
        String imgSrc = null;

        Pattern p = Pattern.compile(imgRegex);
        Matcher m = p.matcher(html);
//        Matcher m = p.matcher(htmlString);

        while(m.find()) {
            imgSrc = m.group(1);
        }
        return imgSrc;
    }

    private void getWeather(String currentLocation) {

        forecastViewPagerAdapter = new ForecastViewPagerAdapter(getSupportFragmentManager(),
                getFragments(currentLocation));
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(forecastViewPagerAdapter);
    }

    private List<Fragment> getFragments(String locationString){
        final List<Fragment> fragmentList = new ArrayList<>();

        new ForecastData().getForecast(new ForecastListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Forecast> forecastArrayList) {
                fragmentList.clear();

                String html = forecastArrayList.get(0).getDescriptionHTML();
                Picasso.get().load(getImageUrl(html)).into(icon);

                locationText.setText(String.format("%s,\n%s", forecastArrayList.get(0).getCity(), forecastArrayList.get(0).getRegion()));
                currentTempText.setText(String.format("%sF", forecastArrayList.get(0).getCurrentTemperature()));
                String[] date = forecastArrayList.get(0).getDate().split(" ");
                //Sat, 10 Nov 2018 06:00 AM AKST
                String splitDate = "Today " + date[0] + " " + date[1] + " " + date[2] + " " + date[3];

                currentDate.setText(splitDate);

                for(int i = 0; i < forecastArrayList.size(); i++){
                    Forecast forecast = forecastArrayList.get(i);
                    ForecastFragment forecastFragment = ForecastFragment.newInstance(forecast);
                    fragmentList.add(forecastFragment);
                }
                forecastViewPagerAdapter.notifyDataSetChanged();
            }
        }, locationString);
        return fragmentList;
    }
}
