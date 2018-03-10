package com.olivialabath.austinallergyalert;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by olivialabath on 1/16/18.
 */


@Entity(tableName = "allergens")
public class AllergenEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int count;
    public String date;
}
