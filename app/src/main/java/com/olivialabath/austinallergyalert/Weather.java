package com.olivialabath.austinallergyalert;

import org.joda.time.LocalDate;

/**
 * Created by olivialabath on 3/19/18.
 */

public class Weather {

    private LocalDate date;
    private int hiTemp;
    private int loTemp;
    private String icon;

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getHiTemp() {
        return hiTemp;
    }
    public void setHiTemp(int hiTemp) {
        this.hiTemp = hiTemp;
    }

    public int getLoTemp() {
        return loTemp;
    }
    public void setLoTemp(int loTemp) {
        this.loTemp = loTemp;
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String toString(){
        return "(date: " + date.toString() + ", hi: " + hiTemp + ", lo: " + loTemp + ", icon: " + icon + ")";
    }
}
