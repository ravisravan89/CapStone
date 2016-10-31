package com.ravisravan.capstone.UI.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.ravisravan.capstone.beans.LocationReminder;
import com.ravisravan.capstone.beans.Reminder;
import com.ravisravan.capstone.beans.LocationBean;
import com.ravisravan.capstone.utils.NavigationUtils;
import com.wefika.flowlayout.FlowLayout;

import java.util.HashSet;

public class CreateLocationReminderActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    private static final LatLng HYDERABAD = new LatLng(17.3850, 78.4867);
    private TextView tv_pick_location;
    private TextView tv_link_call_contact;
    private TextView tv_link_mesage_contact;
    private EditText et_message_text;
    private TextInputEditText et_title;
    private TextInputEditText et_description;
    private LocationBean selectedLocation = new LocationBean();
    private FrameLayout map_frame;
    private SupportMapFragment mapFragment;
    private Marker mMarker;
    private static final int PICK_LOCATION_REQUEST = 100;
    private static final int PICK_CALL_CONTACT_REQUEST = 200;
    private static final int PICK_MESSAGE_CONTACT_REQUEST = 300;
    private static final int PICK_MESSAGE_REQUEST = 400;
    private HashSet<Uri> messageContactsList;
    private LocationReminder mLocationReminder;
    private Reminder mReminder;
    private RadioGroup priority_group;
    private RadioGroup frequency_group;
    private FlowLayout flow_layout;
    private TextInputLayout ttl_title;
    private TextInputLayout ttl_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_location_reminder);
        initialiseUIComponents();
        setOnClickListeners();
        setTitle(getString(R.string.location_reminder_title));
        mLocationReminder = new LocationReminder();
        mReminder = new Reminder();
        mReminder.setType(Constants.REMINDER_LOCATION);
    }

    private void initialiseUIComponents() {
        tv_pick_location = (TextView) findViewById(R.id.tv_pick_location);
        map_frame = (FrameLayout) findViewById(R.id.map_frame);
        tv_link_call_contact = (TextView) findViewById(R.id.tv_link_call_contact);
        tv_link_mesage_contact = (TextView) findViewById(R.id.tv_link_mesage_contact);
        et_message_text = (EditText) findViewById(R.id.et_message_text);
        et_title = (TextInputEditText) findViewById(R.id.et_title);
        et_description = (TextInputEditText) findViewById(R.id.et_description);
        priority_group = (RadioGroup) findViewById(R.id.priority_group);
        frequency_group = (RadioGroup) findViewById(R.id.frequency_group);
        flow_layout = (FlowLayout) findViewById(R.id.flow_layout);
        ttl_description = (TextInputLayout) findViewById(R.id.ttl_description);
        ttl_title = (TextInputLayout) findViewById(R.id.ttl_title);
    }

    private void setOnClickListeners() {
        tv_pick_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateLocationReminderActivity.this, LocationSelectionActivity.class);
                intent.putExtra(ExtrasConstants.SELECTED_LOCATION, selectedLocation);
                startActivityForResult(intent, PICK_LOCATION_REQUEST);
            }
        });

        tv_link_call_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPickContact(PICK_CALL_CONTACT_REQUEST);
            }
        });

        tv_link_mesage_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPickContact(PICK_MESSAGE_CONTACT_REQUEST);
            }
        });

//        tv_select_message.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CreateLocationReminderActivity.this, MessagesActivity.class);
//                intent.putExtra(ExtrasConstants.SELECT_MESSAGE, true);
//                startActivityForResult(intent, PICK_MESSAGE_REQUEST);
//            }
//        });
    }

    private void callPickContact(int requestCode) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if (NavigationUtils.isCallableIntent(pickContactIntent, CreateLocationReminderActivity.this)) {
            startActivityForResult(pickContactIntent, requestCode);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        LatLng latLng = new LatLng(selectedLocation.getLatitude(), selectedLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMarker = mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(latLng).zoom(10).build()));
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(CreateLocationReminderActivity.this, LocationSelectionActivity.class);
                intent.putExtra(ExtrasConstants.SELECTED_LOCATION, selectedLocation);
                startActivityForResult(intent, PICK_LOCATION_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_LOCATION_REQUEST:
                    selectedLocation = (LocationBean) data.getSerializableExtra(ExtrasConstants.SELECTED_LOCATION);
                    if (mapFragment == null) {
                        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.image_map);
                        mapFragment.getMapAsync(this);
                        tv_pick_location.setText(selectedLocation.getDisplayAddress());
                        map_frame.setVisibility(View.VISIBLE);
                    } else {
                        mMarker.remove();
                        LatLng latLng = new LatLng(selectedLocation.getLatitude(), selectedLocation.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                        mMarker = mGoogleMap.addMarker(markerOptions);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(latLng).zoom(15).build()));
                    }
                    break;
                case PICK_CALL_CONTACT_REQUEST:
                    processContactPickResult(data, PICK_CALL_CONTACT_REQUEST);
                    break;
                case PICK_MESSAGE_CONTACT_REQUEST:
                    processContactPickResult(data, PICK_MESSAGE_CONTACT_REQUEST);
                    break;
                case PICK_MESSAGE_REQUEST:
                    String message = data.getStringExtra(ExtrasConstants.SELECTED_MESSAGE);
                    //tv_select_message.setText(message);
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {

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
                phones.moveToFirst();
                String cNumber = phones.getString(phones.getColumnIndex("data1"));
                String cName = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                switch (request) {
                    case PICK_CALL_CONTACT_REQUEST:
                        tv_link_call_contact.setText(cName + "\n" + cNumber);
                        break;
                    case PICK_MESSAGE_CONTACT_REQUEST:
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
                        //tv_link_mesage_contact.setText(messageContactsList.size() + " Contact Linked");
                        break;
                }
                Log.e("number is:", cNumber);
            } else {
                //TODO: show snack bar to intimate there is no phone number.
            }
        } else {
            //TODO: show snack bar to show something has gone wrong.
        }
    }

    private void createLocationReminder() {
        createReminder();
    }

    private void createReminder() {
        String title = et_title.getText().toString();
        if (TextUtils.isEmpty(title)) {
            ttl_title.setErrorEnabled(true);
            ttl_title.setError("Title is empty");
//            et_title.setError("Title is empty");
            return;
        } else {
            ttl_title.setErrorEnabled(false);
            ttl_title.setError(null);
            mReminder.setTitle(title);
        }
        String description = et_description.getText().toString();
        if (TextUtils.isEmpty(description)) {
            ttl_description.setErrorEnabled(true);
            ttl_description.setError("Description is empty");
//            et_description.setError("Description is empty");
            return;
        } else {
            ttl_description.setErrorEnabled(false);
            ttl_description.setError(null);
            mReminder.setDescription(description);
        }
        int priority = priority_group.getCheckedRadioButtonId();
        switch (priority) {
            case R.id.priority_high:
                mReminder.setPriority(Constants.PRIORITY_HIGH);
                break;
            case R.id.priority_medium:
                mReminder.setPriority(Constants.PRIORITY_MEDIUM);
                break;
            case R.id.priority_low:
                mReminder.setPriority(Constants.PRIORITY_LOW);
                break;
        }
        int frequency = frequency_group.getCheckedRadioButtonId();
        switch (frequency) {
            case R.id.frequency_once:
                mReminder.setFrequency(Constants.LOCATION_FREQUENCY_ONCE);
                break;
            case R.id.frequency_always:
                mReminder.setFrequency(Constants.LOCATION_FREQUENCY_EVERYTIME);
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
            createLocationReminder();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
