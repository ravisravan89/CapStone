package com.ravisravan.capstone.beans;

import android.net.Uri;

import com.ravisravan.capstone.Constants.Constants;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by ravisravankumar on 23/10/16.
 */
public class Reminder implements Serializable {

    private long id;
    private String title;
    private String description;
    private int reminderType;
    private Uri callContactUri;
    private HashSet<Uri> messageContactUris;
    private long startDatems;
    private long endDatems;
    private LocationBean locationBean;
    private String messageText;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReminderType() {
        return reminderType;
    }

    public void setReminderType(int reminderType) {
        this.reminderType = reminderType;
    }

    public Uri getCallContactUri() {
        return callContactUri;
    }

    public void setCallContactUri(Uri callContactUri) {
        this.callContactUri = callContactUri;
    }

    public HashSet<Uri> getMessageContactUris() {
        return messageContactUris;
    }

    public void setMessageContactUris(HashSet<Uri> messageContactUris) {
        this.messageContactUris = messageContactUris;
    }

    public long getStartDatems() {
        return startDatems;
    }

    public void setStartDatems(long startDatems) {
        this.startDatems = startDatems;
    }

    public long getEndDatems() {
        return endDatems;
    }

    public void setEndDatems(long endDatems) {
        this.endDatems = endDatems;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public LocationBean getLocationBean() {
        return locationBean;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }
}
