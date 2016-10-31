package com.ravisravan.capstone.data;

import android.provider.BaseColumns;

/**
 * Created by ravi.kumar on 10/19/2016.
 */
public class LookupsContract {

    public static final class ReminderStateLkpTable implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "reminder_state";
        //Column names
        public static final String COLUMN_REMINDER_STATE_ID = "reminder_state_id";       //0       //1
        public static final String COLUMN_REMINDER_STATE_VALUE = "reminder_state_value";//inactive //active
    }

    public static final class ReminderTypeLkpTable implements BaseColumns {
        //Table Name
        public static final String TABLE_NAME = "reminder_type";
        //Column names
        public static final String COLUMN_REMINDER_TYPE_ID = "reminder_type_id";
        public static final String COLUMN_REMINDER_TYPE_VALUE = "reminder_type_value";
    }

    public static final class PriorityLkpTable {
        //Table Name
        public static final String TABLE_NAME = "priority";
        //Column names
        public static final String COLUMN_ID = "priority_id";
        public static final String COLUMN_PRIORITY_VALUE = "priority_value";
    }

    public static final class LocationFrequencyLkpTable {
        //Table Name
        public static final String TABLE_NAME = "location_frequency";
        //Column names
        public static final String COLUMN_ID = "frequency_id";
        public static final String COLUMN_PRIORITY_VALUE = "priority_value";
    }
}
