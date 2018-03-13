package com.olivialabath.austinallergyalert;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by olivialabath on 11/8/17.
 */

public class CustomCalendarView extends LinearLayout
{
    // internal components
    private LinearLayout header;
    private ImageView btnPrevMonth;
    private ImageView btnNextMonth;
    private TextView txtDate;
    private GridView grid;
    private OnDateClickListener mOnDateClickListener;
    private OnMonthChangedListener mOnMonthChangedListener;

//    private TextView prevView;
//    private int prevBGColor;
//    private int prevTextColor;
//    private LocalDate prevDate;

    private List<LocalDate> days;
    private List<Rating> ratings;
    private final LocalDate calendarLowerBound = new LocalDate(2017, 1, 1);
    private final LocalDate calendarUpperBound = LocalDate.now().plusMonths(12).withDayOfMonth(1);
    private final LocalDate currentDate = LocalDate.now();
    private LocalDate selectedDate = LocalDate.now();
    private LocalDate displayDate = LocalDate.now();

//    private AppDatabase db;

    private final String TAG = "CustomCalendarView";

    public CustomCalendarView(Context context)
    {
        super(context);
        init(context);
        updateCalendar();
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        updateCalendar();
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        mOnDateClickListener = listener;
    }

    public void setOnMonthChangedListener(OnMonthChangedListener listener){
        mOnMonthChangedListener = listener;
    }

    public interface OnDateClickListener {
        void onDateClick(@NonNull LocalDate selectedDay, @NonNull TextView view, @NonNull String note);
    }

    public interface OnMonthChangedListener {
        void onMonthChange(@NonNull LocalDate displayDate);
    }

    /**
     * Load component XML layout
     */
    private void init(Context context)
    {
        // initialize the database
//        db = AppDatabase.getInstance(context);

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.custom_calendar_view, this);

        // layout is inflated, assign local variables to components
        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrevMonth = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNextMonth = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);

        // set the prev/next month click listeners
        btnPrevMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDate = displayDate.minusMonths(1);
                testPrevMonthButton();
                if(mOnMonthChangedListener != null)
                    mOnMonthChangedListener.onMonthChange(displayDate);
                updateCalendar();
            }
        });

        btnNextMonth.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDate = displayDate.plusMonths(1);
                testNextMonthButton();if(mOnMonthChangedListener != null)
                    mOnMonthChangedListener.onMonthChange(displayDate);
                updateCalendar();
            }
        });

        // set the month button colors
        btnPrevMonth.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        btnNextMonth.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);


        grid.setOnItemClickListener(onDayClickedListener);

    }

    private void updateCalendar()
    {
        days = new ArrayList<>();
        ratings = new ArrayList<Rating>();

        // determine the cell for current month's beginning
        LocalDate calendar = displayDate.withDayOfMonth(1);
        int monthBeginningCell = (calendar.getDayOfWeek()) % 7;
        Log.d(TAG, "monthBegin: " + monthBeginningCell);

        // move custom_calendar_view backwards to the beginning of the week
        calendar = calendar.minusDays(monthBeginningCell);

        // fill cells (42 days custom_calendar_view)
        while (days.size() < 42)
        {
            days.add(calendar);
            calendar = calendar.plusDays(1);
        }
        Log.d(TAG, "Days = " + Arrays.toString(days.toArray()));

        // update grid
//        loadRatingsByMonth();
//        grid.setOnItemClickListener(onDayClickedListener);
//        grid.setAdapter(new CalendarAdapter());

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
        txtDate.setText(displayDate.toString("MMM yyyy"));
    }

    public void setRatings(List<Rating> ratings){
        this.ratings = ratings;
        grid.setAdapter(new CalendarAdapter());
    }

    private AdapterView.OnItemClickListener onDayClickedListener = new AdapterView.OnItemClickListener() {

        private TextView prevView;
        private int prevBGColor;
        private int prevTextColor;
        private LocalDate prevDate;
//        private int prevRating;

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            prevDate = selectedDate;
            selectedDate = days.get(position);
            Log.i(TAG, "SelectedDate = " + selectedDate.toString());

            TextView tv = (TextView) view;

            //
            if(prevView != null && position < ratings.size()){
                if(selectedDate.getMonthOfYear() == displayDate.getMonthOfYear()
                    && selectedDate.getYear() == displayDate.getYear()) {
                    prevView.setBackgroundColor(getBGColor(ratings.get(position).getRating()));
                    prevView.setTextColor(Color.BLACK);
                }
                else{
                    prevView.setBackgroundColor(0xFBFBFB);
                    prevView.setTextColor(Color.LTGRAY);
                }
            }

            prevView = tv;

            // highlight the selected date
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            ((TextView) view).setTextColor(Color.WHITE);

            // update the mOnDateClickListener
            if(mOnDateClickListener != null)
                mOnDateClickListener.onDateClick(selectedDate, tv, "note goes here");
        }
    };

    private class CalendarAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return days.size();
        }

        @Override
        public LocalDate getItem(int position) {
            return days.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            LocalDate date = getItem(position);
//            Log.d(TAG, "Date in getView: " + date.toString());

            TextView tv;
            if(view == null) {
                tv = new TextView(getContext());
            } else {
                tv = (TextView) view;
            }

            // clear styling
            int width = grid.getWidth() / 7;
            int height = grid.getHeight() / 6;
            tv.setLayoutParams(new GridView.LayoutParams(width, height));
            tv.setTypeface(null, Typeface.NORMAL);
            tv.setTextColor(getResources().getColor(R.color.black));
            tv.setGravity(Gravity.CENTER);

            // if this day is outside current month, grey it out
            if (date.getMonthOfYear() != displayDate.getMonthOfYear())
            {
                tv.setTextColor(Color.LTGRAY);
            }
            // if this is the current day, bold it
            else if (date.equals(currentDate))
            {
                tv.setTypeface(null, Typeface.BOLD);
            }

            if(position < ratings.size()
                    && date.getMonthOfYear() == displayDate.getMonthOfYear()
                    && date.getYear() == displayDate.getYear())
                tv.setBackgroundColor(getBGColor(ratings.get(position).getRating()));


            // if the current day is also the selected day, highlight it
            if(date.equals(selectedDate)){
//                prevView = tv;
//                prevBGColor = getBGColor(ratings.get(position).getRating());
//                prevDate = days.get(position);

                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            }

            // set text
            tv.setText(date.dayOfMonth().getAsText());

            return tv;
        }
    }

    private int getBGColor(int rating){
        switch(rating){
            case 1:
                return ContextCompat.getColor(getContext(), R.color.colorTrace);
            case 2:
                return ContextCompat.getColor(getContext(), R.color.colorLow);
            case 3:
                return ContextCompat.getColor(getContext(), R.color.colorMed);
            case 4:
                return ContextCompat.getColor(getContext(), R.color.colorHigh);
            case 5:
                return ContextCompat.getColor(getContext(), R.color.colorVHigh);
            default:
                return 0xFBFBFB;
        }
    }

    private void testPrevMonthButton(){
        // disable the prevMonth button for dates before Jan 1, 2017
        if(displayDate.withDayOfMonth(1).compareTo(calendarLowerBound) <= 0){
            btnPrevMonth.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            btnPrevMonth.setEnabled(false);
        }
        // enable it for months after that date
        else if(!btnNextMonth.isEnabled() && displayDate.compareTo(calendarUpperBound) <= 0){
            btnNextMonth.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            btnNextMonth.setEnabled(true);
        }
    }

    private void testNextMonthButton(){
        // disable the nextMonth button for dates after the current date + 12 months
        if(displayDate.withDayOfMonth(1).compareTo(calendarUpperBound) >= 0){
            btnNextMonth.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
            btnNextMonth.setEnabled(false);
        }
        // enable it for months before that date
        else if(!btnPrevMonth.isEnabled() && displayDate.compareTo(calendarLowerBound) >= 0){
            btnPrevMonth.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            btnPrevMonth.setEnabled(true);
        }
    }
}
