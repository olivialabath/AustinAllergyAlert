package com.olivialabath.austinallergyalert;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by olivialabath on 3/5/18.
 */

@Dao
public interface RatingDAO {
    @Query("SELECT * FROM rating")
    List<Rating> getAll();

    @Query("SELECT * FROM rating WHERE epochDays BETWEEN :from AND :to")
    List<Rating> loadFromRange(long from, long to);

    @Query("SELECT * FROM rating WHERE epochDays = :epochDays")
    Rating loadByDate(long epochDays);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRating(Rating rating);

}
