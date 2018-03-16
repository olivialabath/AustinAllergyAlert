package com.olivialabath.austinallergyalert;

import android.support.annotation.NonNull;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Arrays;

/**
 * Created by olivialabath on 6/19/17.
 */

@DynamoDBTable(tableName = "Allergens")
public class Allergen implements Comparable<Allergen>{

    private String name;
    private AllergenData.AllergenType type;
    private int count;
    private AllergenData.AllergenLevel level;
    private long date;


    public Allergen() {}

    public Allergen(String name, int count, long epochDays){
        this.name = name;
        setType();
        this.count = count;
        setLevel();
        this.date = epochDays;
    }

    @DynamoDBHashKey
    public long getDate() {
        return date;
    }
    public void setDate(long epochDays) {
        this.date = epochDays;
    }

    @DynamoDBRangeKey
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
//        setType();
    }

    @DynamoDBAttribute
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
//        setLevel();
    }

    @DynamoDBIgnore
    public AllergenData.AllergenType getType(){
        return this.type;
    }
    public void setType(){
        // Mold
        if(name.equalsIgnoreCase("Mold"))
            this.type = AllergenData.AllergenType.Mold;
            // Grass
        else if(name.equalsIgnoreCase("Grass"))
            this.type = AllergenData.AllergenType.Grass;
            // Weed
        else if(Arrays.asList(AllergenData.Weeds).contains(name))
            this.type = AllergenData.AllergenType.Weed;
            //Tree
        else if(Arrays.asList(AllergenData.Trees).contains(name))
            this.type = AllergenData.AllergenType.Tree;
            // Other
        else
            this.type = AllergenData.AllergenType.Other;
    }

    @DynamoDBIgnore
    public AllergenData.AllergenLevel getLevel(){
        return this.level;
    }
    public void setLevel() {
        int row = this.type.getValue();

        // Trace
        if(count < AllergenData.AllergenLevels[row][0])
            this.level = AllergenData.AllergenLevel.Trace;
        // Low
        else if(count < AllergenData.AllergenLevels[row][1])
            this.level = AllergenData.AllergenLevel.Low;
        // Medium
        else if(count < AllergenData.AllergenLevels[row][2])
            this.level = AllergenData.AllergenLevel.Medium;
        // High
        else if(count < AllergenData.AllergenLevels[row][3])
            this.level = AllergenData.AllergenLevel.High;
            // Very_High
        else
            this.level = AllergenData.AllergenLevel.Very_High;
    }

    public String toString(){
        return "(Name: " + name + ", Type: " + type + ", Count: " + count + ", Level: " + level + ", Date: " + date + ")";
    }

    @Override
    public int compareTo(@NonNull Allergen allergen) {
        return this.count - allergen.getCount();
    }

    /* static class used to determine info about an allergen */

    private static class AllergenData {

        public static final String[] Weeds = {"Pigweed", "Ragweed", "Marsh Elder"};
        public static final String[] Trees = {"Cedar", "Elm", "Oak", "Ash", "Mesquite", "Pecan",
                "Privet", "Sycamore", "Mulberry", "Willow", "Red Juniper Berry", "Sage", "Acacia",
                "Birch", "Hackberry", "Poplar", "Cottonwood", "Pine"};


        /* enum describing the category the Allergen falls into */
        public static enum AllergenType {
            Mold(0), Grass(1), Weed(2), Tree(3), Other(4);
            private final int value;
            private AllergenType(int value) { this.value = value; }
            public int getValue() { return value; }
        }

        public static final String[] AllergenLevelStrings = {"Trace", "Low", "Medium", "High", "Very High"};

        /* enum describing how low or high Allergen.count is from Trace to Very_High */
        public static enum AllergenLevel {
            Trace(0), Low(1), Medium(2), High(3), Very_High(4);
            private final int value;
            private AllergenLevel(int value) { this.value = value; }
            public int getValue() { return value; }
        }

        /* used to determine the AllergenLevel of the various allergen types
        *  1st number in each row is the lower bound of Low, 2nd is the lower bound of Medium, etc. */
        public static final int[][] AllergenLevels = {{50, 300, 1000, 10000}, // MOLD
                {15, 50,  100, 200},      // GRASS
                {15, 50, 100, 500},      // WEED
                {15, 50, 100, 1500},     // TREE
                {15, 50, 100, 1000}};   // OTHER

    }
}
