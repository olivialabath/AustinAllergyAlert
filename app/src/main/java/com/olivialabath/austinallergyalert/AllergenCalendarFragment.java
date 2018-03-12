package com.olivialabath.austinallergyalert;

import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by olivialabath on 6/18/17.
 */
public class AllergenCalendarFragment extends Fragment implements RatingDialogFragment.EditRatingDialogListener{

    private CustomCalendarView mCalendarView;
    private TextView mDateTV;
    private ImageButton mEditButton;

    private LocalDate mSelectedDate = LocalDate.now();
    private LocalDate mCurrentDate = LocalDate.now();
    private final SimpleDateFormat calendarDateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
    private final SimpleDateFormat dialogDateFormat = new SimpleDateFormat("EEEE MMMM d");
    private final String TAG = "AllergenCalendarFrag";

    private AppDatabase db;
    private List<Rating> ratings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.allergen_calendar_fragment,container,false);

        // initialize the database
        db = AppDatabase.getInstance(getContext());
        loadAllRatings();
//        loadRatingsByMonth();

        // initialize the selected date textview
        mDateTV = (TextView) v.findViewById((R.id.calendar_date_tv));
        mDateTV.setText(mSelectedDate.toString("EEE MMM d, yyyy"));

        // initialize the edit button
        mEditButton = (ImageButton) v.findViewById(R.id.rating_edit_button);
        mEditButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        // initialize the custom_calendar_view and set the onDateClickListener
        mCalendarView = (CustomCalendarView) v.findViewById(R.id.calendar);
        mCalendarView.setOnDateClickListener(new CustomCalendarView.OnDateClickListener(){
            String note = "this is where the note should go";
            @Override
            public void onDateClick(@NonNull LocalDate selectedDay, @NonNull TextView tv, @NonNull String note){
                // change the date textview and selected date in this class
                mDateTV.setText(selectedDay.toString("EEE MMM d, yyyy"));
                mSelectedDate = selectedDay;

                // disable the edit button if selected day is in the future
                if((mSelectedDate.getYear() == mCurrentDate.getYear()
                        && mSelectedDate.getDayOfYear() > mCurrentDate.getDayOfYear())
                        || mSelectedDate.getYear() > mCurrentDate.getYear()){
                    Log.i(TAG, "disabling edit button");
                    mEditButton.setEnabled(false);
                    mEditButton.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
                }
                // if it's in past or present and currently disabled, enable it
                else {
                    Log.i(TAG, "enabling edit button");
                    mEditButton.setEnabled(true);
                    mEditButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

        return v;
    }

    private void showRatingDialog(){
        Calendar c = Calendar.getInstance();
        c.setTime(mSelectedDate.toDate());
        String dateString = mSelectedDate.toString("EEEE MMMM d") + CalendarHelper.getDayOfMonthSuffix(c);

//        Rating rating = ratings.get(mSelectedDate.get(Calendar.DAY_OF_MONTH - 1));

        RatingDialogFragment ratingDialog = RatingDialogFragment.newInstance(dateString, 0, "this is a note");
        ratingDialog.setTargetFragment(this, 300);
        ratingDialog.show(getFragmentManager(), "rating");
    }

    @Override
    public void onSave(int rating, String note) {
        Log.i(TAG, "rating: " + rating + ", note: " + note);
        insertRating(rating, note);
    }

    private void loadAllRatings(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ratings = db.ratingDAO().getAll();
                Log.d(TAG, "All Ratings: " + Arrays.toString(ratings.toArray()));
            }
        }).start();
    }

    private void loadRatingsByMonth(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // get the current month's ratings
//                ratings = db.ratingDAO().loadByMonth(mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH));
                Log.i(TAG, "Ratings: " + Arrays.toString(ratings.toArray()));
            }
        }).start();
    }

    private void insertRating(final int rating, final String note){

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.ratingDAO().insertRating(new Rating(mSelectedDate.toDateTimeAtStartOfDay().getMillis() / 86400000, rating, note));
                Log.i(TAG, "saving note for " + mSelectedDate.toString());
            }
        }).start();
    }

}
