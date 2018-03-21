package com.olivialabath.austinallergyalert;

import android.util.Log;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by olivialabath on 3/20/18.
 */

public class WeatherParser {
    private final String TAG = "WeatherParser";

    public Weather[] parse(String data){
        Weather[] weatherArray = new Weather[5];

        try{
            JSONObject jsonObject = new JSONObject(data);
            JSONObject daily = jsonObject.getJSONObject("daily");

            JSONArray forecast = daily.getJSONArray("data");

            for(int i = 0; i < 5; ++i){
                Weather weather = new Weather();
                JSONObject day = forecast.getJSONObject(i);

                // set the date
                weather.setDate(new LocalDate((day.getLong("time") + 3600 * 5)* 1000));

                // set the high and low
                weather.setLoTemp((int) day.getDouble("temperatureMin"));
                weather.setHiTemp((int) day.getDouble("temperatureMax"));

                // set the icon string
                weather.setIcon(day.getString("icon"));

                weatherArray[i] = weather;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weatherArray;
    }

}
