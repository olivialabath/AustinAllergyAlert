package com.olivialabath.austinallergyalert;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by olivialabath on 3/20/18.
 */

public class WeatherView extends LinearLayout {

    private final String TAG = "WeatherView";

    private Weather[] weather;
    private Context context;

    private ImageView day1Image;
    private TextView day1Temps;
    private TextView day1Text;

    private ImageView day2Image;
    private TextView day2Temps;
    private TextView day2Text;

    private ImageView day3Image;
    private TextView day3Temps;
    private TextView day3Text;

    private ImageView day4Image;
    private TextView day4Temps;
    private TextView day4Text;

    private ImageView day5Image;
    private TextView day5Temps;
    private TextView day5Text;

    private ImageView[] images;
    private TextView[] temps;
    private TextView[] dates;

    public WeatherView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public WeatherView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }


    private void init(){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weather_view, this);

        day1Image = (ImageView) findViewById(R.id.day_1_image);
        day1Temps = (TextView) findViewById(R.id.day_1_temp);
        day1Text = (TextView) findViewById(R.id.day_1_text);

        day2Image = (ImageView) findViewById(R.id.day_2_image);
        day2Temps = (TextView) findViewById(R.id.day_2_temp);
        day2Text = (TextView) findViewById(R.id.day_2_text);

        day3Image = (ImageView) findViewById(R.id.day_3_image);
        day3Temps = (TextView) findViewById(R.id.day_3_temp);
        day3Text = (TextView) findViewById(R.id.day_3_text);

        day4Image = (ImageView) findViewById(R.id.day_4_image);
        day4Temps = (TextView) findViewById(R.id.day_4_temp);
        day4Text = (TextView) findViewById(R.id.day_4_text);

        day5Image = (ImageView) findViewById(R.id.day_5_image);
        day5Temps = (TextView) findViewById(R.id.day_5_temp);
        day5Text = (TextView) findViewById(R.id.day_5_text);

        images = new ImageView[] {day1Image, day2Image, day3Image, day4Image, day5Image};
        temps = new TextView[] {day1Temps, day2Temps, day3Temps, day4Temps, day5Temps};
        dates = new TextView[] {day1Text, day2Text, day3Text, day4Text, day5Text};
    }

    public void setWeather(Weather[] weather){
        this.weather = weather;
        setViews();
    }

    private void setViews(){
        for(int i = 0; i < weather.length; ++i){
            Weather day = weather[i];

            // set the image
            images[i].setImageResource(getImage(day.getIcon()));

            // set the temps and day of the week
            if(i == 0) {
                temps[i].setText(day.getHiTemp() + "° / " + day.getLoTemp() + "°");
                dates[i].setText(day.getDate().toString("EEEE"));
            } else {
                temps[i].setText(day.getHiTemp() + "°\n" + day.getLoTemp() + "°");
                dates[i].setText(day.getDate().toString("EEE"));
            }
        }
    }

    private int getImage(String icon){
        switch (icon) {
            case "clear-day":
            case "clear-night":
                return R.drawable.weather_clear_day;
            case "rain":
                return R.drawable.weather_rain;
            case "snow":
                return R.drawable.weather_snow;
            case "sleet":
                return R.drawable.weather_sleet;
            case "wind":
                return R.drawable.weather_wind;
            case "fog":
                return R.drawable.weather_fog;
            case "cloudy":
                return R.drawable.weather_cloudy;
            case "partly-cloudy-day":
            case "partly-cloudy-night":
                return R.drawable.weather_cloudy_day;
            case "hail":
                return R.drawable.weather_hail;
            case "thunderstorm":
                return R.drawable.weather_thunder;
            case "tornado":
                return R.drawable.weather_tornado;
            default:
                return R.drawable.weather_cloudy_day;
        }
    }
}
