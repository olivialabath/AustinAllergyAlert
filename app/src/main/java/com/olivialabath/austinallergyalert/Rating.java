package com.olivialabath.austinallergyalert;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by olivialabath on 3/5/18.
 */

@Entity
public class Rating implements Comparable<Rating>{
    @PrimaryKey
    private long epochDays;

    @ColumnInfo
    private int rating;

    @ColumnInfo
    private String note;

    public Rating(long epochDays, int rating, String note) {
        this.epochDays = epochDays;
        this.rating = rating;
        this.note = note;
    }

    public long getEpochDays() {
        return epochDays;
    }
    public void setEpochDays(long epochDays) {
        this.epochDays = epochDays;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public String toString(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(epochDays * 86400000);
        return "(date: " + c.getTime().toString() + ", rating: " + rating + ", note: " + note + ")";
    }

    public Calendar getDate(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(epochDays * 86400000);
        return c;
    }

    @Override
    public int compareTo(@NonNull Rating rating) {
        return (int) (epochDays - rating.epochDays);
    }
}
