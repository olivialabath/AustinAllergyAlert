package com.olivialabath.austinallergyalert;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

/**
 * Created by olivialabath on 1/16/18.
 */

@Database(entities = {Rating.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    private static AppDatabase instance;
    public abstract RatingDAO ratingDAO();
    private static final String TAG = "AppDatabase";

    public static AppDatabase getInstance(Context context){
        if(instance == null){
            //Log.i(TAG, "creating a new db instance");
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "rating").build();
        }
        return instance;
    }
}
