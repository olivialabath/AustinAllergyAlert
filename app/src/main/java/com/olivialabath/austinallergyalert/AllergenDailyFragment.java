package com.olivialabath.austinallergyalert;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by olivialabath on 6/18/17.
 */
public class AllergenDailyFragment extends Fragment {

    private final String TAG = "AllergenDailyFragment";

    private ColumnChartView mChart;
    private ColumnChartData data;
    private Allergen[] mAllergens;
    private View mRootview;
    private WeatherView mWeatherView;
    private Calendar mCurrentDate = CalendarHelper.now();
    private Calendar mQueryDate = CalendarHelper.getQueryDate();
    private TextView mDateReported;
    private SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, yyyy");

    private DynamoDBMapper mapper;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        /* create the dynamodb mapper */
        CognitoCachingCredentialsProvider credentials = new CognitoCachingCredentialsProvider(
                getContext(),
                Config.identityPool, // Identity pool ID
                Regions.US_EAST_2 // Region
        );
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentials);
        ddbClient.setRegion(Region.getRegion(Regions.US_EAST_2));
        mapper = new DynamoDBMapper(ddbClient);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /* inflate views */
        mRootview = inflater.inflate(R.layout.allergen_daily_fragment,container,false);
        mWeatherView = (WeatherView) mRootview.findViewById(R.id.daily_weather_view);

        /* get today's allergens */
        AllergenTask allergenTask = new AllergenTask();
        allergenTask.execute();

        /* get the 5 day forecast */
        getWeather();

        Log.i(TAG, "QueryDate: " + mQueryDate.getTime().toString());

        return mRootview;
    }

    public void setChartValues() {
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        for(int i = 0; i < mAllergens.length; ++i) {
            /* set the column count and color */
            values = new ArrayList<SubcolumnValue>();
            values.add(new SubcolumnValue(mAllergens[i].getCount(), getColumnColor(mAllergens[i])));
            Column c = new Column(values);

            /* set the x axis labels */
            AxisValue av = new AxisValue(i);
            av.setLabel(mAllergens[i].getName());
            axisValues.add(av);
            c.setHasLabels(true);

            columns.add(c);
        }
        data = new ColumnChartData(columns);

        /* x axis stuff */
        Axis axisX = new Axis(axisValues);
        axisX.setTextColor(ContextCompat.getColor(getContext(), android.R.color.primary_text_light));
        axisX.setTextSize(13);
        axisX.setName("Allergens");
        /* if more than 6 allergens are found, tilt the labels
                 * to prevent values from overlapping */
        if(mAllergens.length >= 6){
            axisX.setHasTiltedLabels(true);
        }
        data.setAxisXBottom(axisX);

        /* y axis stuff */
        Axis axisY = new Axis().setHasLines(true);
        axisY.setTextColor(ContextCompat.getColor(getContext(), android.R.color.primary_text_light));
        axisY.setTextSize(13);
        axisY.setName("Allergen Counts");
        data.setAxisYLeft(axisY);
    }

    private int getColumnColor(Allergen a) {
        switch(a.getLevel()) {
            case Trace:
                return ContextCompat.getColor(getContext(), R.color.colorTrace);
            case Low:
                return ContextCompat.getColor(getContext(), R.color.colorLow);
            case Medium:
                return ContextCompat.getColor(getContext(), R.color.colorMed);
            case High:
                return ContextCompat.getColor(getContext(), R.color.colorHigh);
            case Very_High:
                return ContextCompat.getColor(getContext(), R.color.colorVHigh);
            default:
                return ContextCompat.getColor(getContext(), R.color.black);
        }
    }

    private Allergen[] getTestAllergens(){
        Calendar c = mQueryDate;
        Allergen[] allergenArray = {
                new Allergen("Pine", 5, CalendarHelper.getEpochDays(c)),
                new Allergen("Elm", 12, CalendarHelper.getEpochDays(c)),
                new Allergen("Pigweed", 39, CalendarHelper.getEpochDays(c)),
                new Allergen("Mold", 49, CalendarHelper.getEpochDays(c)),
                new Allergen("Ragweed", 92, CalendarHelper.getEpochDays(c)),
                new Allergen("Cedar", 150, CalendarHelper.getEpochDays(c)),
                new Allergen("Grass", 211, CalendarHelper.getEpochDays(c))
        };

        return allergenArray;
    }


    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            //Log.d(TAG, "column pressed");
            Allergen a = mAllergens[columnIndex];
            String toastString = a.getName() + "\nType: "+ a.getType().toString() +
                    "\nCount: " + a.getCount() + "\nLevel: " + a.getLevel().toString();
            Toast.makeText(getContext(), toastString, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onValueDeselected() { }

    }

    private class AllergenTask extends AsyncTask<Void, Void, List<Allergen>>{
        final String QUERY_TAG = "queryAllergensByDate";

        @Override
        protected List<Allergen> doInBackground(Void... voids) {
            long epochDayDate = CalendarHelper.getEpochDays(mQueryDate);
            Log.i(QUERY_TAG, "epochDate: " + epochDayDate);

            Allergen a = new Allergen();
            a.setDate(epochDayDate);
            DynamoDBQueryExpression<Allergen> queryExpression = new DynamoDBQueryExpression<Allergen>()
                    .withHashKeyValues(a);
            List<Allergen> allergenList = mapper.query(Allergen.class, queryExpression);

//                Log.i("queryAllergensByDate", Arrays.toString(result.toArray()));

            if (allergenList.size() == 0) {
                mQueryDate = CalendarHelper.prevWeekDay(mQueryDate);
                Log.i(QUERY_TAG, "No allergens found for current query date, setting query date to " + mQueryDate.getTime().toString());
                return doInBackground();
            }
            else{
                return allergenList;
            }
        }

        @Override
        protected void onPostExecute(List<Allergen> allergenList){
            mAllergens = new Allergen[allergenList.size()];
            mAllergens = allergenList.toArray(mAllergens);
//                mAllergens = getTestAllergens();

            for(Allergen allergen : mAllergens){
                allergen.setType();
                allergen.setLevel();
            }
            Arrays.sort(mAllergens);
            Log.i(QUERY_TAG, Arrays.toString(mAllergens));

                /* set chart data and values */
            mChart = (ColumnChartView) mRootview.findViewById(R.id.allergen_chart);
            mChart.setOnValueTouchListener(new ValueTouchListener());
            setChartValues();
            mChart.setColumnChartData(data);


                /* set the "Reported on date" textview */
            mDateReported = mRootview.findViewById(R.id.date_reported_tv);
            long date = mAllergens[0].getDate();
            mDateReported.setText(sdf.format(new Date((date + 1) * 86400000)));
        }

    }

    private void getWeather(){

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://api.darksky.net/forecast/" + Config.DarkSkyAPIKey + "/30.2672,-97.7431?exclude=currently,minutely,hourly,alerts,flags";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        WeatherParser wp = new WeatherParser();
                        Weather[] weather = wp.parse(response.toString());
                        Log.i("getWeather", Arrays.toString(weather));
                        mWeatherView.setWeather(weather);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);


    }

}
