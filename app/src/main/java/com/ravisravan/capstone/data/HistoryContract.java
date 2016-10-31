package com.ravisravan.capstone.data;

import android.provider.BaseColumns;

/**
 * Created by ravi.kumar on 10/19/2016.
 */
public class HistoryContract {

    public static final class Reminders implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "reminders_history";
        //Column names
        public static final String COLUMN_REMINDER_ID = "reminder_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRIORITY = "priority"; //High 2 medium 1 low 0
        public static final String COLUMN_REMINDER_TYPE = "reminder_type"; //location 1 time 2
        public static final String COLUMN_STATE = "state"; //active 1 inactive 2
        public static final String COLUMN_CREATED_DATE = "create_date";
        public static final String COLUMN_LOG_DATE = "logged_date";
        public static final String COLUMN_START_DATE = "start_date";
        public static final String COLUMN_END_DATE = "end_date";
        public static final String COLUMN_CALL_ENABLED = "call_enabled";
        public static final String COLUMN_MESSAGE_ENABLED = "message_enabled";
    }

    public static final class LocationReminders implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "location_reminders_history";
        //Column names
        public static final String COLUMN_HSTORY_ID = "reminder_history_id";
        public static final String COLUMN_LAT = "latitude";
        public static final String COLUMN_LNG = "longitude";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_RADIUS = "radius"; //radius in meters
        public static final String COLUMN_FREQUENCY = "frequency"; //frequency is Every time = 2, next time = 1
    }

    public static final class Contacts_Table implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "contacts_history";
        //Column names
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
    }

    public static final class Call_Contact_Table implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "call_contact_history";
        //Column names
        public static final String COLUMN_HISTORY_REMINDER_ID = "history_reminder_id";
        public static final String COLUMN_HISTORY_CONTACT_ID = "history_contact_id";
        public static final String COLUMN_HISTORY_PHONE_NUMBER = "history_phone_number";
    }

    public static final class MessageLkpTable implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "message_history_lkp";
        //Column names
        public static final String COLUMN_CONTENT = "content";
        protected static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_CONTENT + " TEXT UNIQUE NOT NULL " +
                " );";
    }

    public static final class Message_Contact_Table implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "message_contact_history";
        //Column names
        public static final String COLUMN_REMINDER_ID = "history_reminder_id";//reminder._id
        public static final String COLUMN_CONTACT_ID = "history_contact_id";//contact._id
        public static final String COLUMN_MESSAGE_ID = "history_message_id";//message._id
    }
}
