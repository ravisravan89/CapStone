package com.ravisravan.capstone.UI.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatDrawableManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.ravisravan.capstone.Constants.Constants;
import com.ravisravan.capstone.Constants.ExtrasConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.beans.LocationBean;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationSelectionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    final String TAG = LocationSelectionActivity.class.getSimpleName();
    GoogleMap mGoogleMap;
    String displayAddress = "";
    Place mPlace;
    private TextView tv_location_text;
    private TextView tv_latlng_text;
    private FloatingActionButton fab_pick_location;
    private LocationBean selectedLocation;
    private LatLng currentLatLng;
    private Circle mCircle;
    private SeekBar radius_seekbar;
    private Bitmap ic_thumb;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);
//        Bitmap bitmap = getBitmapFromVectorDrawable(R.drawable.ic_thumb);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_thumb, options);
        selectedLocation = (LocationBean) getIntent().getSerializableExtra(ExtrasConstants.SELECTED_LOCATION);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tv_location_text = (TextView) findViewById(R.id.tv_location_text);
        tv_latlng_text = (TextView) findViewById(R.id.tv_latlng_text);
        fab_pick_location = (FloatingActionButton) findViewById(R.id.fab_pick_location);
        radius_seekbar = (SeekBar) findViewById(R.id.radius_seekbar);

        radius_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = 100 + progress * (900 / 100);
                Bitmap bitmap = getBitmapFromVectorDrawable(R.drawable.ic_thumb);
                ic_thumb = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas c = new Canvas(ic_thumb);
                String text = Integer.toString(radius);
                Paint p = new Paint();
                p.setTypeface(Typeface.DEFAULT_BOLD);
                p.setTextSize(12);
                p.setColor(0xFFFFFFFF);
                int width = (int) p.measureText(text);
//                int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
                int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
                c.drawText(text, (ic_thumb.getWidth() - width) / 2, yPos, p);
                seekBar.setThumb(new BitmapDrawable(getResources(), ic_thumb));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                int radius = 100 + progress * (900 / 100); //900 is the difference between min 100 and max 1000mts
                mCircle.setRadius(radius);
            }
        });
        radius_seekbar.setProgress(1);
        fab_pick_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                selectedLocation.setLatitude(currentLatLng.latitude);
                selectedLocation.setLongitude(currentLatLng.longitude);
                selectedLocation.setDisplayAddress(displayAddress);
                //TODO: remove static variable.
                selectedLocation.setRadiusInMeters(100);
                selectedLocation.setInitialised(true);
                returnIntent.putExtra(ExtrasConstants.SELECTED_LOCATION, selectedLocation);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            launchPlaceSelectionIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchPlaceSelectionIntent() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mPlace = place;
                Log.i("", "Place: " + place.getName());
                if (mGoogleMap != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(place.getLatLng()).zoom(14f).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        ////mGoogleMap.setOnCameraMoveListener(this);
        mGoogleMap.setOnCameraIdleListener(this);
        if (selectedLocation.isInitialised()) {
            currentLatLng = new LatLng(selectedLocation.getLatitude(), selectedLocation.getLongitude());
            CameraPosition cp = CameraPosition.builder().target(currentLatLng).zoom(14f).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
        } else {

        }
    }

    @Override
    public void onCameraIdle() {
        currentLatLng = mGoogleMap.getCameraPosition().target;
        if (mCircle == null) {
            mCircle = mGoogleMap.addCircle(new CircleOptions().center(currentLatLng)
                    .fillColor(Color.parseColor("#6664B5F6"))
                    .strokeColor(Color.CYAN)
                    .strokeWidth(Constants.CIRCLE_STROKE_WIDTH)
                    .radius(Constants.CIRCLE_MINIMUM_RADIUS));
        } else {
            mCircle.setCenter(currentLatLng);
        }
        Log.e(TAG, currentLatLng.toString());
        if (mPlace == null) {
            getAddress(currentLatLng.latitude, currentLatLng.longitude);
        } else {
            displayAddress = mPlace.getName() + "\n" + mPlace.getAddress();
            tv_location_text.setLines(2);
            tv_location_text.setText(displayAddress);
            currentLatLng = mPlace.getLatLng();
            tv_latlng_text.setText("Lat : " + currentLatLng.latitude + " Lng : " + currentLatLng.longitude);
            mPlace = null;
        }
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String sublocality = address.getSubLocality();
                result.append(TextUtils.isEmpty(sublocality) ? "" : sublocality + ", ");
                String subAdminArea = address.getSubAdminArea();
                result.append(TextUtils.isEmpty(subAdminArea) ? "" : subAdminArea + ", ");
                String locality = address.getLocality();
                result.append(TextUtils.isEmpty(locality) ? "" : locality + ", ");
                String countryName = address.getCountryName();
                result.append(TextUtils.isEmpty(countryName) ? "" : countryName);
                displayAddress = result.toString();

            } else {
                displayAddress = "No Address Found";
            }
        } catch (IOException e) {
            displayAddress = e.getMessage();
            Log.e("tag", e.getMessage());
        }
        tv_latlng_text.setText("Lat : " + latitude + " Lng : " + longitude);
        tv_location_text.setLines(1);
        tv_location_text.setText(displayAddress);
        return result.toString();
    }

    private Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(this, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
