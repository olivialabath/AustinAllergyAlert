package com.olivialabath.austinallergyalert;

import org.junit.Test;

import java.util.Arrays;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UnitTests {
    @Test
    public void testPrevWeekDay1(){
        Calendar c = CalendarHelper.now();
        c.set(Calendar.DAY_OF_WEEK, 4);
        c = CalendarHelper.prevWeekDay(c);
        assertEquals(3, c.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void testPrevWeekDay2(){
        Calendar c = CalendarHelper.now();
        c.set(Calendar.DAY_OF_WEEK, 7);
        c = CalendarHelper.prevWeekDay(c);
        assertEquals(6, c.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void testPrevWeekDay3(){
        Calendar c = CalendarHelper.now();
        c.set(Calendar.DAY_OF_WEEK, 1);
        c = CalendarHelper.prevWeekDay(c);
        assertEquals(6, c.get(Calendar.DAY_OF_WEEK));
    }

}