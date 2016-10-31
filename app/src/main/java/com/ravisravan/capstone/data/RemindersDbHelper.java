package com.ravisravan.capstone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
        sqLiteDatabase.execSQL(ReminderContract.MessageLkpTable.CREATE_TABLE);
        //For current data.
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
}
