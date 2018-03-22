package com.olivialabath.austinallergyalert;

import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private ImageView mRatingFace;
    private TextView mNotesText;

    private Rating selectedRating;
    private LocalDate mSelectedDate = LocalDate.now();
    private LocalDate mDisplayDate = LocalDate.now();
    private LocalDate mCurrentDate = LocalDate.now();
    private final SimpleDateFormat calendarDateFormat = new SimpleDateFormat("EEE MMM d, yyyy");
    private final SimpleDateFormat dialogDateFormat = new SimpleDateFormat("EEEE MMMM d");
    private final String TAG = "AllergenCalendarFrag";
    private final long MILLIS_IN_DAY = 86400000;

    private AppDatabase db;
    private List<Rating> ratings = new ArrayList<Rating>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.allergen_calendar_fragment,container,false);

        // initialize the database and load the current month's ratings
        db = AppDatabase.getInstance(getContext());
        LoadRatingsByMonthTask task = new LoadRatingsByMonthTask();
        task.execute();

        // initialize the selected date textview
        mDateTV = (TextView) v.findViewById((R.id.calendar_date_tv));
        mDateTV.setText(mSelectedDate.toString("EEE MMM d, yyyy"));

        // init the notes and rating face
        mRatingFace = (ImageView) v.findViewById(R.id.calendar_face);
        mNotesText = (TextView) v.findViewById(R.id.calendar_notes);

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
            public void onDateClick(@NonNull LocalDate selectedDay, @NonNull TextView tv, @NonNull int position){
                // change the date textview and selected date in this class
                mDateTV.setText(selectedDay.toString("EEE MMM d, yyyy"));
                mSelectedDate = selectedDay;
                selectedRating = ratings.get(mCalendarView.getDays().indexOf(mSelectedDate));

                // update the face and notes
                mRatingFace.setImageResource(getFace(ratings.get(position).getRating()));
                mNotesText.setText(ratings.get(position).getNote());

                // disable the edit button if selected day is in the future
                if((mSelectedDate.getYear() == mCurrentDate.getYear()
                        && mSelectedDate.getDayOfYear() > mCurrentDate.getDayOfYear())
                        || mSelectedDate.getYear() > mCurrentDate.getYear()){
//                    Log.i(TAG, "disabling edit button");
                    mEditButton.setEnabled(false);
                    mEditButton.setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_ATOP);
                }
                // if it's in past or present and currently disabled, enable it
                else {
//                    Log.i(TAG, "enabling edit button");
                    mEditButton.setEnabled(true);
                    mEditButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

        mCalendarView.setOnMonthChangedListener(new CustomCalendarView.OnMonthChangedListener() {
            @Override
            public void onMonthChange(@NonNull LocalDate displayMonth) {
                mDisplayDate = displayMonth;
                // load the new month's ratings
                LoadRatingsByMonthTask task = new LoadRatingsByMonthTask();
                task.execute();
            }
        });

        return v;
    }

    private void showRatingDialog(){
        Calendar c = Calendar.getInstance();
        c.setTime(mSelectedDate.toDate());
        String dateString = mSelectedDate.toString("EEEE MMMM d") + CalendarHelper.getDayOfMonthSuffix(c);

//        Rating rating = new Rating()
        RatingDialogFragment ratingDialog = RatingDialogFragment.newInstance(dateString, selectedRating.getRating(), selectedRating.getNote());
        ratingDialog.setTargetFragment(this, 300);
        ratingDialog.show(getFragmentManager(), "rating");
    }

    @Override
    public void onSave(int rating, String note) {
        // update the face and note
        mRatingFace.setImageResource(getFace(rating));
        mNotesText.setText(note);

        Log.i(TAG, "saving rating: " + rating + ", note: " + note);
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

    private class LoadRatingsByMonthTask extends AsyncTask<Void, Void, List<Rating>>{

        @Override
        protected List<Rating> doInBackground(Void... voids) {
            // get the from and to values for the db query
            LocalDate startDate = mDisplayDate.withDayOfMonth(1);
            int monthBeginCell = (startDate.getDayOfWeek()) % 7;
            startDate = startDate.minusDays(monthBeginCell);
            LocalDate endDate = startDate.plusDays(41);
            Log.d(TAG, "startDate: " + startDate.toString() + ", endDate: " + endDate.toString());
            long from = startDate.toDateTimeAtStartOfDay().getMillis() / MILLIS_IN_DAY;
            long to = endDate.toDateTimeAtStartOfDay().getMillis() / MILLIS_IN_DAY;
            Log.d(TAG, "from: "  + from + ", to: " + to);

            // get the ratings
            List<Rating> ratingsList = db.ratingDAO().loadFromRange(from, to);

            // create empty ratings for any days that don't have ratings
            for (int i = 0; i < 42; ++i) {
                long day = startDate.plusDays(i).toDateTimeAtStartOfDay().getMillis() / MILLIS_IN_DAY;
                Rating r = new Rating(day, -1, "");
                if (i >= ratingsList.size() || day != ratingsList.get(i).getEpochDays())
                    ratingsList.add(i, r);
                else
                    r = ratingsList.get(i);

//                Log.d(TAG, "doInBackground: selectedDate = " + mSelectedDate.toString() + ", startDate = " + startDate.toString());
            }

            Log.i(TAG, "selected month's Ratings: " + Arrays.toString(ratingsList.toArray()));

            return ratingsList;
        }

        @Override
        protected void onPostExecute(List<Rating> ratingList){
            ratings = ratingList;
            if(selectedRating == null)
                selectedRating = ratings.get(mCalendarView.getDays().indexOf(mSelectedDate));
            mRatingFace.setImageResource(getFace(selectedRating.getRating()));
            mNotesText.setText(selectedRating.getNote());

            mCalendarView.setRatings(ratings);
        }
    }

    private void insertRating(final int rating, final String note){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Rating r = new Rating(mSelectedDate.toDateTimeAtStartOfDay().getMillis() / MILLIS_IN_DAY, rating, note);

                // update ratings if the new rating is in the current month
                int index = mCalendarView.getDays().indexOf(mSelectedDate);
                if(index > -1){
                    ratings.set(index, r);
                    mCalendarView.setRating(index, r);
                }

                // update the selectedRating
                selectedRating = r;

                db.ratingDAO().insertRating(r);
                Log.i(TAG, "saving note for " + mSelectedDate.toString());
            }
        }).start();
    }

    public int getFace(int rating){
        switch(rating) {
            case 0:
                return R.mipmap.ic_very_good_round;
            case 1:
                return R.mipmap.ic_good_round;
            case 2:
                return R.mipmap.ic_neutral_round;
            case 3:
                return R.mipmap.ic_bad_round;
            case 4:
                return R.mipmap.ic_very_bad_round;
            default:
                return 0;
        }
    }

}
