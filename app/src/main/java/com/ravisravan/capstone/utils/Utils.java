package com.ravisravan.capstone.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.widget.TextView;

import com.ravisravan.capstone.R;
import com.ravisravan.capstone.data.RemindersDbHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ravisravankumar on 04/11/16.
 */
public class Utils {

    public static String formaDate(long dateInms) {
        Date selectedDate = new Date(dateInms);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE,dd MMM yyyy", Locale.getDefault());
        String displayDate = sdf.format(selectedDate);
        return displayDate;
    }

    public static String[] getContactData(String contactId, Context context) {
        Cursor phones = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                null, null);
        String cNumber = "";
        String cName = "";
        if (phones.moveToFirst()) {
            cNumber = phones.getString(phones.getColumnIndex("data1"));
            cName = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            return new String[]{cName, cNumber};
        } else {
            //TODO: show data from local db.
            RemindersDbHelper helper = new RemindersDbHelper(context);
            return helper.getContactDetails(contactId);
        }
    }
}
