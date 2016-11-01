package com.ravisravan.capstone.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by ravisravankumar on 22/10/16.
 */
public class RemindersProvider extends ContentProvider {


    private RemindersDbHelper mOpenHelper;
    static final int MESSAGES = 100;
    static final int MESSAGE = 101;
    static final int CONTACTS = 200;
    static final int CONTACT = 201;
    static final int REMINDERS = 300;
    static final int REMINDER = 301;
    static final int LOCATION = 400;

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mOpenHelper = new RemindersDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "messages"
            case MESSAGES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ReminderContract.MessageLkpTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REMINDERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(ReminderContract.Reminders.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MESSAGES:
                return ReminderContract.MessageLkpTable.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MESSAGES: {
                long _id = db.insert(ReminderContract.MessageLkpTable.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ReminderContract.MessageLkpTable.buildMessageUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CONTACTS: {
                long _id = db.insert(ReminderContract.ContactsTable.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ReminderContract.ContactsTable.buildContactUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REMINDERS: {
                long _id = db.insert(ReminderContract.Reminders.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ReminderContract.Reminders.buildReminderUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(ReminderContract.LocationReminders.TABLE_NAME, null, values);
                //TODO: we have to test if we need to return reminder id or locaiton reminder Id
                //Also we need to notify the uri for reminder that is using this data. when it is updated
                if (_id > 0)
                    returnUri = ReminderContract.LocationReminders.buildLocationReminderUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MESSAGES:
                rowsDeleted = db.delete(
                        ReminderContract.MessageLkpTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MESSAGES:
                rowsUpdated = db.update(ReminderContract.MessageLkpTable.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ReminderContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, ReminderContract.PATH_MESSAGES, MESSAGES);
        matcher.addURI(authority, ReminderContract.PATH_MESSAGES + "/#", MESSAGE);

        matcher.addURI(authority, ReminderContract.PATH_CONTACTS, CONTACTS);
        matcher.addURI(authority, ReminderContract.PATH_CONTACTS + "/#", CONTACT);

        matcher.addURI(authority, ReminderContract.PATH_REMINDERS, REMINDERS);
        matcher.addURI(authority, ReminderContract.PATH_REMINDERS + "/#", REMINDER);

        matcher.addURI(authority, ReminderContract.PATH_LOCATION, LOCATION);
        return matcher;
    }
}
