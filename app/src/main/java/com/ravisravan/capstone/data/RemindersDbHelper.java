package com.ravisravan.capstone.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ravisravan.capstone.R;
import com.ravisravan.capstone.beans.LocationBean;

import java.util.ArrayList;

/**
 * Created by ravi.kumar on 10/19/2016.
 */
public class RemindersDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "reminders.db";

    public RemindersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //For Messages
        //TODO: remove this it might not be needed later.
        sqLiteDatabase.execSQL(ReminderContract.MessageLkpTable.CREATE_TABLE);
        //For current data.
        //For Contacts.
        sqLiteDatabase.execSQL(ReminderContract.ContactsTable.CREATE_TABLE);
        //For Reminders
        sqLiteDatabase.execSQL(ReminderContract.Reminders.CREATE_TABLE);
        sqLiteDatabase.execSQL(ReminderContract.ReminderContactTable.CREATE_TABLE);
        sqLiteDatabase.execSQL(ReminderContract.LocationReminders.CREATE_TABLE);
//        sqLiteDatabase.execSQL(ReminderContract.Reminders.CREATE_TABLE);
//        sqLiteDatabase.execSQL(ReminderContract.LocationReminders.CREATE_TABLE);
//        sqLiteDatabase.execSQL(ReminderContract.Contacts_Table.CREATE_TABLE);
//        sqLiteDatabase.execSQL(ReminderContract.Call_Contact_Table.CREATE_TABLE);
//        sqLiteDatabase.execSQL(ReminderContract.Message_Contact_Table.CREATE_TABLE);
        //For history data

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void updateReminderContactTable(String reminderId, ArrayList<ContentValues> messageContactVals) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor oldContactsCursor = db.query(ReminderContract.ReminderContactTable.TABLE_NAME, null,
                ReminderContract.ReminderContactTable.COLUMN_REMINDER_ID + " = ? ",
                new String[]{reminderId}, null, null, null);
        ArrayList<String> selectedContacts = new ArrayList();
        for (ContentValues value : messageContactVals) {
            String selectedContactId = (String) value.get(ReminderContract.ContactsTable.COLUMN_ID);
            selectedContacts.add(selectedContactId);
        }
        while (oldContactsCursor.moveToNext()) {
            String oldContactId = oldContactsCursor.getString(oldContactsCursor.getColumnIndex(ReminderContract.ReminderContactTable.COLUMN_REMINDER_ID));
            if (!selectedContacts.contains(oldContactId)) {
                //this removes contacts that are previously selected and removed now.
                db.delete(ReminderContract.ReminderContactTable.TABLE_NAME,
                        ReminderContract.ReminderContactTable.COLUMN_REMINDER_ID + " = ? AND "
                                + ReminderContract.ReminderContactTable.COLUMN_CONTACT_ID + " = ? ",
                        new String[]{reminderId, oldContactId});
            } else {
                //this removes already inserted contacts from new list
                selectedContacts.remove(oldContactId);
            }
        }
        oldContactsCursor.close();
        //insert the remaining
        for (String selectedNewId : selectedContacts) {
            ContentValues values = new ContentValues();
            values.put(ReminderContract.ReminderContactTable.COLUMN_CONTACT_ID, selectedNewId);
            values.put(ReminderContract.ReminderContactTable.COLUMN_REMINDER_ID, reminderId);
            long id = db.insert(ReminderContract.ReminderContactTable.TABLE_NAME, null, values);
            Log.e("REMINDER CONTACT ID ", "" + id);
        }
    }

    public void removeMessagesForReminder(String reminderId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ReminderContract.ReminderContactTable.TABLE_NAME, ReminderContract.ReminderContactTable.COLUMN_REMINDER_ID + " = ? ",
                new String[]{reminderId});
    }

    public String[] getContactDetails(String contactId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(ReminderContract.ContactsTable.TABLE_NAME, null,
                ReminderContract.ContactsTable.COLUMN_ID + " = ?",
                new String[]{contactId}, null, null, null);
        String cName = c.getString(c.getColumnIndex(ReminderContract.ContactsTable.COLUMN_NAME));
        String cNumber = c.getString(c.getColumnIndex(ReminderContract.ContactsTable.COLUMN_PHONE_NUMBER));
        c.close();
        return new String[]{cName, cNumber};
    }

    public Cursor getReminderContactsCursor(String reminderId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(ReminderContract.ReminderContactTable.TABLE_NAME, null,
                ReminderContract.ReminderContactTable.COLUMN_REMINDER_ID + " = ?",
                new String[]{reminderId}, null, null, null);
        return c;
    }

    public LocationBean getLocationData(String reminderId) {
        LocationBean bean = new LocationBean();
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(ReminderContract.LocationReminders.TABLE_NAME, null,
                ReminderContract.LocationReminders.COLUMN_ID + " = ?",
                new String[]{reminderId}, null, null, null);
        c.moveToNext();
        bean.setDisplayAddress(c.getString(c.getColumnIndex(ReminderContract.LocationReminders.COLUMN_ADDRESS)));
        bean.setInitialised(true);
        bean.setFrequency(c.getInt(c.getColumnIndex(ReminderContract.LocationReminders.COLUMN_FREQUENCY)));
        bean.setRadiusInMeters(c.getDouble(c.getColumnIndex(ReminderContract.LocationReminders.COLUMN_RADIUS)));
        bean.setLatitude(c.getDouble(c.getColumnIndex(ReminderContract.LocationReminders.COLUMN_LAT)));
        bean.setLongitude(c.getDouble(c.getColumnIndex(ReminderContract.LocationReminders.COLUMN_LNG)));
        c.close();
        return bean;
    }
}
