package com.ravisravan.capstone.beans;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by ravisravankumar on 23/10/16.
 */
public class LocationReminder implements Serializable {

    private Reminder reminder;

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }


}
