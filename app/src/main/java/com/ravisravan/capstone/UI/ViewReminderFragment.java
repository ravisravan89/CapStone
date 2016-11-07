package com.ravisravan.capstone.UI;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ravisravan.capstone.Constants.Constants;
import com.ravisravan.capstone.Constants.ExtrasConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.UI.activities.LocationSelectionActivity;
import com.ravisravan.capstone.beans.LocationBean;
import com.ravisravan.capstone.data.ReminderContract;
import com.ravisravan.capstone.data.RemindersDbHelper;
import com.ravisravan.capstone.utils.Utils;
import com.wefika.flowlayout.FlowLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class ViewReminderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //    private OnFragmentInteractionListener mListener;
    private Uri mUri;
    private final int REMINDER_DETAILS = 300;

    public ViewReminderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(ExtrasConstants.REMINDER_URI);
//            mTransitionAnimation = arguments.getBoolean(DetailActivityFragment.DETAIL_TRANSITION_ANIMATION, false);
        }
        View rootView = inflater.inflate(R.layout.fragment_view_reminder, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REMINDER_DETAILS, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    ReminderContract.Reminders._ID + " = ? ",
                    new String[]{mUri.getLastPathSegment()},
                    null
            );
        }
//        ViewParent vp = getView().getParent();
//        if (vp instanceof CardView) {
//            ((View) vp).setVisibility(View.INVISIBLE);
//        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            Log.e("Loaded", data.toString());

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            Toolbar toolbarView = (Toolbar) getView().findViewById(R.id.toolbar);
            String title = data.getString(data.getColumnIndex(ReminderContract.Reminders.COLUMN_TITLE));
            TextView tv_reminder_description = (TextView) getView().findViewById(R.id.tv_reminder_description);
            String description = data.getString(data.getColumnIndex(ReminderContract.Reminders.COLUMN_DESCRIPTION));
            tv_reminder_description.setText(description);

            long createDatems = data.getLong(data.getColumnIndex(ReminderContract.Reminders.COLUMN_CREATED_DATE));
            long startDatems = data.getLong(data.getColumnIndex(ReminderContract.Reminders.COLUMN_START_DATE));
            long endDatems = data.getLong(data.getColumnIndex(ReminderContract.Reminders.COLUMN_END_DATE));

            TextView tv_create_date = (TextView) getView().findViewById(R.id.tv_create_date);
            tv_create_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar_selected, 0, 0, 0);
            tv_create_date.setCompoundDrawablePadding(10);
            tv_create_date.setText(String.format("Reminder Created On : %1s", Utils.formaDate(createDatems)));

            TextView tv_start_date = (TextView) getView().findViewById(R.id.tv_start_date);
            tv_start_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar_selected, 0, 0, 0);
            tv_start_date.setCompoundDrawablePadding(10);
            tv_start_date.setText(String.format("Reminder Start Date : %1s", Utils.formaDate(startDatems)));

            TextView tv_end_date = (TextView) getView().findViewById(R.id.tv_end_date);
            if (endDatems == -1) {
                tv_end_date.setText("Reminds for ever");
                tv_end_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar, 0, 0, 0);
            } else {
                tv_end_date.setText(String.format("Reminder End Date : %1s", Utils.formaDate(endDatems)));
                tv_end_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_calendar_selected, 0, 0, 0);
            }
            tv_end_date.setCompoundDrawablePadding(10);

            TextView call_reminder_tv = (TextView) getView().findViewById(R.id.call_reminder_tv);
            String contactId = data.getString(data.getColumnIndex(ReminderContract.Reminders.COLUMN_CALL_CONTACTID));
            if (!TextUtils.isEmpty(contactId)) {
                call_reminder_tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call_contact_checked, 0, 0, 0);
                String[] contactData = Utils.getContactData(contactId, getActivity());
                call_reminder_tv.setText(String.format(getString(R.string.remind_to_call_contact), contactData[0], contactData[1]));

            } else {
                call_reminder_tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_call_contact_unchecked, 0, 0, 0);
                call_reminder_tv.setText("No Call Reminders");
            }
            call_reminder_tv.setCompoundDrawablePadding(10);

            TextView message_text_tv = (TextView) getView().findViewById(R.id.message_text_tv);
            String messageText = data.getString(data.getColumnIndex(ReminderContract.Reminders.COLUMN_MESSAGE_TEXT));
            if (!TextUtils.isEmpty(messageText)) {
                message_text_tv.setVisibility(View.VISIBLE);
                message_text_tv.setText(messageText);
            }

            TextView auto_message_tv = (TextView) getView().findViewById(R.id.auto_message_tv);
            auto_message_tv.setCompoundDrawablePadding(10);
            //long reminderId = Long.valueOf(mUri.getLastPathSegment());
            RemindersDbHelper helper = new RemindersDbHelper(getActivity());
            Cursor reminderContacts = helper.getReminderContactsCursor(mUri.getLastPathSegment());
            if (reminderContacts.getCount() > 0) {
                auto_message_tv.setText("Send the below message to the contacts displayed");
                auto_message_tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_contact_checked, 0, 0, 0);
                FlowLayout flow_layout = (FlowLayout) getView().findViewById(R.id.flow_layout);
                flow_layout.setVisibility(View.VISIBLE);
                while (reminderContacts.moveToNext()) {
                    String id = reminderContacts.getString(reminderContacts
                            .getColumnIndex(ReminderContract.ReminderContactTable.COLUMN_CONTACT_ID));
                    String[] contactData = Utils.getContactData(id, getActivity());
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    final View layout = inflater.inflate(R.layout.contact_preview_layout, null);
                    FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 5, 5);
                    flow_layout.addView(layout, layoutParams);
                    TextView nameTV = (TextView) layout.findViewById(R.id.contact_name);
                    ImageView remove_contact = (ImageView) layout.findViewById(R.id.remove_contact);
                    remove_contact.setVisibility(View.GONE);
                    nameTV.setText(contactData[0] + "\n" + contactData[1]);
                }
            } else {
                auto_message_tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_message_contact_unchecked, 0, 0, 0);
                auto_message_tv.setText("No Automated Messages");
            }
            reminderContacts.close();

            TextView tv_reminder_details = (TextView) getView().findViewById(R.id.tv_reminder_details);
            int reminderType = data.getInt(data.getColumnIndex(ReminderContract.Reminders.COLUMN_REMINDER_TYPE));
            if (reminderType == Constants.REMINDER_LOCATION) {
                tv_reminder_details.setText("Location Details");
                TextView tv_address = (TextView) getView().findViewById(R.id.tv_address);

                final LocationBean locationBean = helper.getLocationData(mUri.getLastPathSegment());
                tv_address.setText(locationBean.getDisplayAddress());
                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.image_map);
//                SupportMapFragment mapFragment = (SupportMapFragment) getActivity()
//                        .getSupportFragmentManager().findFragmentById(R.id.image_map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.getUiSettings().setMapToolbarEnabled(true);
                        LatLng latLng = new LatLng(locationBean.getLatitude(), locationBean.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                        googleMap.addMarker(markerOptions);
                        googleMap.moveCamera(CameraUpdateFactory
                                .newCameraPosition(CameraPosition.builder().target(latLng).zoom(10).build()));
                    }
                });
                TextView tv_radius = (TextView) getView().findViewById(R.id.tv_radius);
                tv_radius.setText("Radius : " + locationBean.getRadiusInMeters() + " meters");

                TextView tv_frequency = (TextView) getView().findViewById(R.id.tv_frequency);
                if (locationBean.getFrequency() == Constants.LOCATION_FREQUENCY_EVERYTIME) {
                    tv_frequency.setText("Remind every time I am here");
                } else {
                    tv_frequency.setText("Remind next time I am here");
                }
            } else {
                //TODO:set time related UI
            }


            // We need to start the enter transition after the data has loaded
//        if (mTransitionAnimation) {
//            activity.supportStartPostponedEnterTransition();

            if (null != toolbarView) {
                activity.setSupportActionBar(toolbarView);
                TextView toolbar_title = (TextView) toolbarView.findViewById(R.id.toolbar_title);
                toolbar_title.setText(title);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
//        } else {
//            if (null != toolbarView) {
//                Menu menu = toolbarView.getMenu();
//                if (null != menu) menu.clear();
//                toolbarView.inflateMenu(R.menu.menu_details_fragment);
//                finishCreatingMenu(toolbarView.getMenu());
//            }
//        }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
