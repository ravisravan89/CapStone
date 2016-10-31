package com.ravisravan.capstone.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ravi.kumar on 10/19/2016.
 * Here we create DB columns
 */
public class ReminderContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.ravisravan.capstone";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.ravisravan.capstone/messages/ is a valid path for
    // looking at messages data. content://com.ravisravan.capstone/xyz/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "xyz".
    public static final String PATH_MESSAGES = "messages";

    /* Inner class that defines the table contents of the reminder table */
    public static final class Reminders implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "reminders";
        //Column names
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIORITY = "priority"; //High 2 medium 1 low 0
        public static final String COLUMN_REMINDER_TYPE = "reminder_type"; //location 1 time 2
        public static final String COLUMN_STATE = "state"; //active 1 inactive 2
        public static final String COLUMN_CREATED_DATE = "create_date";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_CALL_ENABLED = "call_enabled";
        public static final String COLUMN_MESSAGE_ENABLED = "message_enabled";
        protected static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_TITLE + " TEXT UNIQUE NOT NULL, " +
                COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                COLUMN_PRIORITY + " INTEGER NOT NULL, " + //0 low 1 medium 2 hign
                COLUMN_REMINDER_TYPE + " INTEGER NOT NULL " + // 1 location 2 time
                COLUMN_STATE + " INTEGER NOT NULL " + // 1 active 0 inactive
                COLUMN_CREATED_DATE + " INTEGER NOT NULL " +
                COLUMN_START_DATE + " INTEGER NOT NULL " +
                COLUMN_END_DATE + " INTEGER DEFAULT -1 " + //if -1 means its for ever
                COLUMN_CALL_ENABLED + " INTEGER DEFAULT 0 " + //0 - call not enabled, 1 call enabled
                COLUMN_MESSAGE_ENABLED + " INTEGER DEFAULT 0 " + //0 - message not enabled, 1 message enabled
                " );";
    }

    public static final class LocationReminders implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "location_reminders";
        //Column names
        public static final String COLUMN_ID = "reminder_id";
        public static final String COLUMN_LAT = "latitude";
        public static final String COLUMN_LNG = "longitude";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_RADIUS = "radius"; //radius in meters
        public static final String COLUMN_FREQUENCY = "frequency"; //frequency is Every time = 2, next time = 1
        protected static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_ID + " INTEGER NOT NULL, " +
                COLUMN_LAT + " REAL NOT NULL, " +
                COLUMN_LNG + " REAL NOT NULL, " +
                COLUMN_ADDRESS + " TEXT NOT NULL, " +
                COLUMN_RADIUS + " INTEGER NOT NULL " +
                COLUMN_FREQUENCY + " INTEGER NOT NULL " + //frequency is Every time = 2, next time = 1
                // Set up the remainder_id column as a foreign key to Reminders table.
                " FOREIGN KEY (" + COLUMN_ID + ") REFERENCES " +
                Reminders.TABLE_NAME + " (" + Reminders._ID + "), " +
                // To assure the application have just one location reminder per
                // reminder id, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + COLUMN_ID + ") ON CONFLICT REPLACE " +
                " );";
    }

    public static final class ContactsTable implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "contacts";
        //Column names
        public static final String COLUMN_ID = "device_contact_id";
        public static final String COLUMN_NAME = "contact_name";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        protected static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_ID + " INTEGER UNIQUE NOT NULL, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
                " );";
    }

    public static final class CallContact_able implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "call_contact";
        //Column names
        public static final String COLUMN_REMINDER_ID = "reminder_id";
        public static final String COLUMN_CONTACT_ID = "contact_id";
        protected static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_REMINDER_ID + " INTEGER NOT NULL, " +
                COLUMN_CONTACT_ID + " INTEGER NOT NULL, " +
                // Set up the remainder_id column as a foreign key to Reminders table.
                " FOREIGN KEY (" + COLUMN_REMINDER_ID + ") REFERENCES " +
                Reminders.TABLE_NAME + " (" + Reminders._ID + "), " +
                " FOREIGN KEY (" + COLUMN_CONTACT_ID + ") REFERENCES " +
                ContactsTable.TABLE_NAME + " (" + ContactsTable.COLUMN_ID + ") " +
                " );";
    }

    public static final class MessageLkpTable implements BaseColumns {
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_MESSAGES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MESSAGES;
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MESSAGES).build();

        //Table Name
        public static final String TABLE_NAME = "message";
        //Column names
        public static final String COLUMN_CONTENT = "content";
        protected static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_CONTENT + " TEXT UNIQUE NOT NULL " +
                " );";

        public static Uri buildMessageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class MessageContactTable implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "message_contact";
        //Column names
        public static final String COLUMN_REMINDER_ID = "reminder_id";
        public static final String COLUMN_CONTACT_ID = "contact_id";
        public static final String COLUMN_MESSAGE_ID = "message_id";
        protected static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_REMINDER_ID + " INTEGER NOT NULL, " +
                COLUMN_CONTACT_ID + " INTEGER NOT NULL, " +
                COLUMN_MESSAGE_ID + " INTEGER NOT NULL " +
                // Set up the remainder_id column as a foreign key to Reminders table.
                " FOREIGN KEY (" + COLUMN_REMINDER_ID + ") REFERENCES " +
                Reminders.TABLE_NAME + " (" + Reminders._ID + "), " +
                " FOREIGN KEY (" + COLUMN_CONTACT_ID + ") REFERENCES " +
                ContactsTable.TABLE_NAME + " (" + ContactsTable.COLUMN_ID + "), " +
                " FOREIGN KEY (" + COLUMN_CONTACT_ID + ") REFERENCES " +
                MessageLkpTable.TABLE_NAME + " (" + MessageLkpTable._ID + ") " +
                " );";
    }
}


