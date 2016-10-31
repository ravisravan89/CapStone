package com.ravisravan.capstone.beans;

import android.net.Uri;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by ravisravankumar on 23/10/16.
 */
public class Reminder implements Serializable {

    private String title;
    private String description;
    private int priority;
    private int type;
    private int frequency;
    private Uri callContactUri;
    private HashSet<Uri> messageContactUris;

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
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

}
