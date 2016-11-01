package com.ravisravan.capstone.UI.activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ravisravan.capstone.Constants.Constants;
import com.ravisravan.capstone.Constants.ExtrasConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.beans.LocationBean;
import com.ravisravan.capstone.beans.Reminder;
import com.ravisravan.capstone.data.ReminderContract;
import com.ravisravan.capstone.data.RemindersDbHelper;
import com.ravisravan.capstone.utils.NavigationUtils;
import com.wefika.flowlayout.FlowLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    long mReminderId = -1;
    private View compose_message_view;
    private CheckBox cb_auto_messages;
    private CheckBox cb_call_reminder;
    private final int PICK_LOCATION_REQUEST = 100;
    private final int CALL_CONTACT_REQUEST = 200;
    private final int MESSAGE_CONTACT_REQUEST = 300;
    private HashSet<Uri> messageContactsList;
    private FlowLayout flow_layout;
    private TextView add_contact;
    private TextView tv_pick_location;
    private RadioGroup rg_reminer_type;
    private RadioGroup rg_location_frequency;
    private View location_reminder_view;
    private LocationBean locationBean;
    private SupportMapFragment mapFragment;
    private Marker mMarker;
    private FrameLayout map_frame;
    private GoogleMap mGoogleMap;
    private TextView tv_start_date;
    private TextView tv_end_date;
    private Calendar today = Calendar.getInstance();
    private Calendar startDate;
    private Calendar endDate;
    private ImageView remove_start_date;
    private ImageView remove_end_date;
    private TextInputEditText et_title;
    private TextInputEditText et_description;
    private EditText et_compose_message;
    private TextInputLayout ttl_title;
    private TextInputLayout ttl_description;
    private TextInputLayout ttl_compose_message;
    private Reminder mReminder = new Reminder();
    private Uri mCallContactUri;
    private ContentValues callContactVals;
    private ArrayList<ContentValues> messageContactVals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        readExtras();
        mReminder.setId(mReminderId);
        initialiseViews();
        addInteractionListeners();
    }

    private void initialiseViews() {
        compose_message_view = findViewById(R.id.compose_message_view);
        cb_auto_messages = (CheckBox) findViewById(R.id.cb_auto_messages);
        cb_call_reminder = (CheckBox) findViewById(R.id.cb_call_reminder);
        flow_layout = (FlowLayout) findViewById(R.id.flow_layout);
        add_contact = (TextView) findViewById(R.id.add_contact);
        rg_reminer_type = (RadioGroup) findViewById(R.id.rg_reminer_type);
        location_reminder_view = findViewById(R.id.location_reminder_view);
        tv_pick_location = (TextView) findViewById(R.id.tv_pick_location);
        map_frame = (FrameLayout) findViewById(R.id.map_frame);
        tv_start_date = (TextView) findViewById(R.id.tv_start_date);
        tv_end_date = (TextView) findViewById(R.id.tv_end_date);
        remove_start_date = (ImageView) findViewById(R.id.remove_start_date);
        remove_end_date = (ImageView) findViewById(R.id.remove_end_date);
        et_title = (TextInputEditText) findViewById(R.id.et_title);
        et_description = (TextInputEditText) findViewById(R.id.et_description);
        ttl_title = (TextInputLayout) findViewById(R.id.ttl_title);
        ttl_description = (TextInputLayout) findViewById(R.id.ttl_description);
        ttl_compose_message = (TextInputLayout) findViewById(R.id.ttl_compose_message);
        et_compose_message = (EditText) findViewById(R.id.et_compose_message);
        rg_location_frequency = (RadioGroup) findViewById(R.id.rg_location_frequency);
    }

    private void addInteractionListeners() {
        tv_pick_location.setOnClickListener(this);
        add_contact.setOnClickListener(this);
        tv_start_date.setOnClickListener(this);
        tv_end_date.setOnClickListener(this);
        remove_start_date.setOnClickListener(this);
        remove_end_date.setOnClickListener(this);
        cb_auto_messages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    compose_message_view.setVisibility(View.VISIBLE);
                } else {
                    compose_message_view.setVisibility(View.GONE);
                }
            }
        });
        cb_call_reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pickContact(CALL_CONTACT_REQUEST);
                }
            }
        });
        rg_reminer_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_location_reminder:
                        location_reminder_view.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_time_reminder:
                        location_reminder_view.setVisibility(View.GONE);
                        break;
                }
            }
        });

    }

    private void setStartDate() {
        if (startDate == null)
            startDate = Calendar.getInstance();
        showDateDialog(startDate, tv_start_date, today);
    }

    private void showDateDialog(final Calendar setCalendar, final TextView textView, Calendar minDate) {
        if (minDate == null) {
            minDate = today;
        }
        int year = setCalendar.get(Calendar.YEAR);
        int month = setCalendar.get(Calendar.MONTH);
        int day = setCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int dayOfMonth) {
                setCalendar.set(Calendar.YEAR, selectedYear);
                setCalendar.set(Calendar.MONTH, selectedMonth);
                setCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date selectedDate = setCalendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("EEE,dd MMM yyyy", Locale.getDefault());
                String displayDate = sdf.format(selectedDate);
                textView.setText(displayDate);
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar_selected, 0, 0, 0);
            }

        }, year, month, day);
        dateDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        dateDialog.show();
    }

    private void setEndDate() {
        if (endDate == null)
            endDate = Calendar.getInstance();
        if (startDate != null)
            endDate = startDate;
        showDateDialog(endDate, tv_end_date, startDate);
    }

    private void selectLocation() {
        if (locationBean == null) {
            locationBean = new LocationBean();
        }
        Intent intent = new Intent(AddReminderActivity.this, LocationSelectionActivity.class);
        intent.putExtra(ExtrasConstants.SELECTED_LOCATION, locationBean);
        startActivityForResult(intent, PICK_LOCATION_REQUEST);
    }

    private void pickContact(int requestCode) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if (NavigationUtils.isCallableIntent(pickContactIntent, AddReminderActivity.this)) {
            startActivityForResult(pickContactIntent, requestCode);
        }
    }

    private void readExtras() {
        if (getIntent().hasExtra(ExtrasConstants.REMINDER_ID)) {
            mReminderId = getIntent().getLongExtra(ExtrasConstants.REMINDER_ID, -1L);
        }
    }

    private void processContactPickResult(Intent data, int request) {
        final Uri contactData = data.getData();
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (hasPhone.equalsIgnoreCase("1")) {
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null, null);
                if (phones.moveToFirst()) {
                    String cNumber = phones.getString(phones.getColumnIndex("data1"));
                    String cName = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    switch (request) {
                        case CALL_CONTACT_REQUEST:
                            if (callContactVals == null) {
                                callContactVals = new ContentValues();
                            }
                            String text = String.format(getString(R.string.remind_to_call_contact), cName, cNumber);
                            mCallContactUri = contactData;
                            cb_call_reminder.setText(text);
                            callContactVals.put(ReminderContract.ContactsTable.COLUMN_ID, id);
                            callContactVals.put(ReminderContract.ContactsTable.COLUMN_NAME, cName);
                            callContactVals.put(ReminderContract.ContactsTable.COLUMN_PHONE_NUMBER, cNumber);
                            break;
                        case MESSAGE_CONTACT_REQUEST:
                            if (messageContactsList == null) {
                                messageContactsList = new HashSet<Uri>();
                                messageContactVals = new ArrayList<ContentValues>();
                            }
                            int previousContactSize = messageContactsList.size();
                            messageContactsList.add(contactData);
                            int currentContactsSize = messageContactsList.size();
                            if (currentContactsSize == previousContactSize) {

                            } else {
                                messageContactsList.add(contactData);
                                LayoutInflater inflater = LayoutInflater.from(this);
                                final View layout = inflater.inflate(R.layout.contact_preview_layout, null);
                                FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(0, 0, 5, 5);
                                flow_layout.addView(layout, layoutParams);
                                TextView nameTV = (TextView) layout.findViewById(R.id.contact_name);
                                ImageView remove_contact = (ImageView) layout.findViewById(R.id.remove_contact);
                                remove_contact.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        flow_layout.removeView(layout);
                                        messageContactsList.remove(contactData);
                                    }
                                });
                                nameTV.setText(cName + "\n" + cNumber);
                                ContentValues values = new ContentValues();
                                values.put(ReminderContract.ContactsTable.COLUMN_ID, id);
                                values.put(ReminderContract.ContactsTable.COLUMN_NAME, cName);
                                values.put(ReminderContract.ContactsTable.COLUMN_PHONE_NUMBER, cNumber);
                                messageContactVals.add(values);
                                c.close();
                            }
                            break;
                    }
                } else {
                    //TODO: show snack bar to intimate something has gone wrong.
                }
            } else {
                //TODO: show snack bar to intimate there is no phone number.
            }
        } else {
            //TODO: show snack bar to show something has gone wrong.
        }
    }

    private void processPickLocationResult(Intent data) {
        locationBean = (LocationBean) data.getSerializableExtra(ExtrasConstants.SELECTED_LOCATION);
        tv_pick_location.setText(locationBean.getDisplayAddress());
        tv_pick_location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_reminder_checked, 0, 0, 0);
        if (mapFragment == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.image_map);
            mapFragment.getMapAsync(this);
            map_frame.setVisibility(View.VISIBLE);
        } else {
            mMarker.remove();
            LatLng latLng = new LatLng(locationBean.getLatitude(), locationBean.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            mMarker = mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(latLng).zoom(15).build()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CALL_CONTACT_REQUEST:
                    processContactPickResult(data, CALL_CONTACT_REQUEST);
                    if (mCallContactUri == null) {
                        cb_call_reminder.setChecked(false);
                    }
                    break;
                case MESSAGE_CONTACT_REQUEST:
                    processContactPickResult(data, MESSAGE_CONTACT_REQUEST);
                    break;
                case PICK_LOCATION_REQUEST:
                    processPickLocationResult(data);
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case CALL_CONTACT_REQUEST:
                    if (mCallContactUri == null)
                        cb_call_reminder.setChecked(false);
                    break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        LatLng latLng = new LatLng(locationBean.getLatitude(), locationBean.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMarker = mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(latLng).zoom(10).build()));
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(AddReminderActivity.this, LocationSelectionActivity.class);
                intent.putExtra(ExtrasConstants.SELECTED_LOCATION, locationBean);
                startActivityForResult(intent, PICK_LOCATION_REQUEST);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pick_location:
                selectLocation();
                break;
            case R.id.add_contact:
                pickContact(MESSAGE_CONTACT_REQUEST);
                break;
            case R.id.tv_start_date:
                setStartDate();
                break;
            case R.id.tv_end_date:
                setEndDate();
                break;
            case R.id.remove_start_date:
                startDate = null;
                tv_start_date.setText(R.string.start_date);
                tv_start_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0);
                break;
            case R.id.remove_end_date:
                endDate = null;
                tv_end_date.setText(R.string.end_date);
                tv_end_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            if (isValidReminder()) {
                saveReminder();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isValidReminder() {
        String title = et_title.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ttl_title.setErrorEnabled(true);
            ttl_title.setError("Title missing");
            return false;
        } else {
            ttl_title.setErrorEnabled(false);
            ttl_title.setError(null);
        }
        String description = et_description.getText().toString();
        if (TextUtils.isEmpty(description)) {
            ttl_description.setErrorEnabled(true);
            ttl_description.setError("Description missing");
            return false;
        } else {
            ttl_description.setErrorEnabled(false);
            ttl_description.setError(null);
        }
        mReminder.setTitle(title);
        mReminder.setDescription(description);
        mReminder.setStartDatems(startDate == null ? today.getTimeInMillis() : startDate.getTimeInMillis());
        mReminder.setEndDatems(endDate == null ? -1 : endDate.getTimeInMillis());
        if (cb_call_reminder.isChecked()) {
            if (mCallContactUri == null) {
                Snackbar.make(findViewById(R.id.call_message_options_view), "Select a contact to call", Snackbar.LENGTH_SHORT).show();
                return false;
            } else {
                mReminder.setCallContactUri(mCallContactUri);
            }
        } else {
            mReminder.setCallContactUri(null);
        }
        String composedMessage = et_compose_message.getText().toString();
        if (cb_auto_messages.isChecked()) {
            if (TextUtils.isEmpty(composedMessage)) {
                ttl_compose_message.setErrorEnabled(true);
                ttl_compose_message.setError("Message is missing");
                return false;
            } else {
                ttl_compose_message.setErrorEnabled(false);
                ttl_compose_message.setError(null);
            }
            if (messageContactsList == null) {
                Snackbar.make(findViewById(android.R.id.content), "Select contacts to send sms", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            if (messageContactsList.size() == 0) {
                Snackbar.make(findViewById(android.R.id.content), "Select contact to send sms", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            mReminder.setMessageContactUris(messageContactsList);
            mReminder.setMessageText(composedMessage);
        } else {
            mReminder.setMessageContactUris(null);
            mReminder.setMessageText(null);
        }
        switch (rg_reminer_type.getCheckedRadioButtonId()) {
            case R.id.rb_location_reminder:
                mReminder.setReminderType(Constants.REMINDER_LOCATION);
                break;
            case R.id.rb_time_reminder:
                mReminder.setReminderType(Constants.REMINDER_TIME);
                break;
        }
        if (mReminder.getReminderType() == Constants.REMINDER_LOCATION) {
            //TODO: set time bean to null
            if (locationBean == null) {
                Snackbar.make(findViewById(android.R.id.content), "Select a location to set reminder", Snackbar.LENGTH_LONG).show();
                return false;
            }
            if (!locationBean.isInitialised()) {
                Snackbar.make(findViewById(android.R.id.content), "Select a location to set reminder", Snackbar.LENGTH_LONG).show();
                return false;
            }
            switch (rg_location_frequency.getCheckedRadioButtonId()) {
                case R.id.rb_always:
                    locationBean.setFrequency(Constants.LOCATION_FREQUENCY_EVERYTIME);
                    break;
                case R.id.rb_once:
                    locationBean.setFrequency(Constants.LOCATION_FREQUENCY_ONCE);
                    break;
            }
            mReminder.setLocationBean(locationBean);
        } else {
            //TODO: Set Time bean validatiions after class is created.
            mReminder.setLocationBean(null);
            mReminder.setReminderType(Constants.REMINDER_TIME);
        }
        //Tyoe is set in radio group checked change listener.
        //time validations pending, frequency has to be set.
        return true;
    }

    private void saveReminder() {
        //First insert contacts in the db and get their ID.
        Snackbar.make(findViewById(android.R.id.content), "Saving Reminder ", Snackbar.LENGTH_LONG).show();

        ContentValues remindersValue = new ContentValues();
        if (cb_call_reminder.isChecked()) {
            if (callContactVals != null) {
                remindersValue.put(ReminderContract.Reminders.COLUMN_CALL_CONTACTID, (String) callContactVals.get(ReminderContract.ContactsTable.COLUMN_ID));
                try {
                    getContentResolver().insert(ReminderContract.ContactsTable.CONTENT_URI, callContactVals);
                } catch (SQLException e) {
                    //e.printStackTrace();
                } catch (UnsupportedOperationException e) {

                }
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Internal Error please retry. ", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }
        if (cb_auto_messages.isChecked()) {
            if (messageContactVals != null) {
                remindersValue.put(ReminderContract.Reminders.COLUMN_MESSAGE_TEXT, et_compose_message.getText().toString());
                for (ContentValues value : messageContactVals) {
                    try {
                        getContentResolver().insert(ReminderContract.ContactsTable.CONTENT_URI, value);
                    } catch (SQLException e) {

                    } catch (UnsupportedOperationException e) {

                    }
                }
            } else {
                Snackbar.make(findViewById(android.R.id.content), "Internal Error please retry. ", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }
        remindersValue.put(ReminderContract.Reminders.COLUMN_TITLE, mReminder.getTitle());
        remindersValue.put(ReminderContract.Reminders.COLUMN_DESCRIPTION, mReminder.getDescription());
        remindersValue.put(ReminderContract.Reminders.COLUMN_START_DATE, mReminder.getStartDatems());
        remindersValue.put(ReminderContract.Reminders.COLUMN_END_DATE, mReminder.getEndDatems());
        remindersValue.put(ReminderContract.Reminders.COLUMN_CREATED_DATE, today.getTimeInMillis());
        remindersValue.put(ReminderContract.Reminders.COLUMN_REMINDER_TYPE, mReminder.getReminderType());
        try {
            Uri insertedReminderUri = getContentResolver().insert(ReminderContract.Reminders.CONTENT_URI, remindersValue);
            String reminderId = insertedReminderUri.getLastPathSegment();
            //This part maps the contacts with reminders to send sms
            RemindersDbHelper helper = new RemindersDbHelper(this);
            if (cb_auto_messages.isChecked()) {
                helper.updateReminderContactTable(reminderId, messageContactVals);
            } else {
                helper.removeMessagesForReminder(reminderId);
            }
            //location data in location table
            ContentValues locationVals = new ContentValues(); // reminderId, lat, lng,  address, radius, frequency
            locationVals.put(ReminderContract.LocationReminders.COLUMN_ID, reminderId);
            locationVals.put(ReminderContract.LocationReminders.COLUMN_LAT, mReminder.getLocationBean().getLatitude());
            locationVals.put(ReminderContract.LocationReminders.COLUMN_LNG, mReminder.getLocationBean().getLongitude());
            locationVals.put(ReminderContract.LocationReminders.COLUMN_ADDRESS, mReminder.getLocationBean().getDisplayAddress());
            locationVals.put(ReminderContract.LocationReminders.COLUMN_RADIUS, mReminder.getLocationBean().getRadiusInMeters());
            locationVals.put(ReminderContract.LocationReminders.COLUMN_FREQUENCY, mReminder.getLocationBean().getFrequency());
            Uri insertedLocationReminderUri = getContentResolver().insert(ReminderContract.LocationReminders.CONTENT_URI, locationVals);
            String locationreminderId = insertedLocationReminderUri.getLastPathSegment();
            Log.e("Reminder id ", reminderId);
            Log.e("locationreminderId", locationreminderId);
            finish();
        } catch (SQLException e) {

        } catch (UnsupportedOperationException e) {

        }
    }
}
