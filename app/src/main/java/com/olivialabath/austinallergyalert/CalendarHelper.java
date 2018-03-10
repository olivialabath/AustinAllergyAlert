package com.olivialabath.austinallergyalert;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by olivialabath on 3/1/18.
 * need to replace all instances of Calendar with joda LocalDate and get rid of this class
 */

public abstract class CalendarHelper {
    public static Calendar now(){
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("US/Central"));
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c;
    }

    public static Calendar getQueryDate(){
        Calendar c = now();
        if(c.get(Calendar.DAY_OF_WEEK) == 1){
            c.add(Calendar.DAY_OF_YEAR, -2);
        } else if(c.get(Calendar.DAY_OF_WEEK) == 7){
            c.add(Calendar.DAY_OF_YEAR, -1);
        }

        return c;
    }

    public static Calendar prevWeekDay(Calendar c){
        if(c.get(Calendar.DAY_OF_WEEK) == 1){
            c.add(Calendar.DAY_OF_YEAR, -2);
        } else {
            c.add(Calendar.DAY_OF_YEAR, -1);
        }
        return c;
    }

    public static long getEpochDays(Calendar c){
        return c.getTimeInMillis() / 86400000;
    }

    public static String getDayOfMonthSuffix(Calendar c) {
        int n = c.get(Calendar.DAY_OF_MONTH);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
}
