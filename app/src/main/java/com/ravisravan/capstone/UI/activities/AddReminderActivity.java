package com.ravisravan.capstone.UI.activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
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
import com.ravisravan.capstone.Constants.ExtrasConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.beans.LocationBean;
import com.ravisravan.capstone.utils.NavigationUtils;
import com.wefika.flowlayout.FlowLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class AddReminderActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    int mRemainderId = -1;
    private View compose_message_view;
    private CheckBox cb_auto_messages;
    private CheckBox cb_call_reminder;
    private final int PICK_LOCATION_REQUEST = 100;
    private final int CALL_CONTACT_REQUEST = 200;
    private final int MESSAGE_CONTACT_REQUEST = 300;
    private ContentValues callContactContentValues;
    private HashSet<Uri> messageContactsList;
    private FlowLayout flow_layout;
    private TextView add_contact;
    private TextView tv_pick_location;
    private RadioGroup rg_reminer_type;
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
    private TextInputLayout ttl_title;
    private TextInputLayout ttl_description;
    private TextInputLayout ttl_compose_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        readExtras();
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
            mRemainderId = getIntent().getIntExtra(ExtrasConstants.REMINDER_ID, -1);
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
                            String text = String.format(getString(R.string.remind_to_call_contact), cName, cNumber);
                            if (callContactContentValues == null)
                                callContactContentValues = new ContentValues();
                            //callContactContentValues.put(); //id
                            //callContactContentValues.put(); //name
                            //callContactContentValues.put(); //number
                            cb_call_reminder.setText(text);
                            break;
                        case MESSAGE_CONTACT_REQUEST:
                            if (messageContactsList == null) {
                                messageContactsList = new HashSet<Uri>();
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
                    if (callContactContentValues == null) {
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
                    if (callContactContentValues == null)
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
                break;
            case R.id.remove_end_date:
                endDate = null;
                tv_end_date.setText(R.string.end_date);
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
            createReminderBean();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean createReminderBean() {
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
        return true;
    }
}
