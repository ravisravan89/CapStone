package com.ravisravan.capstone.UI.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.ravisravan.capstone.Constants.ExtrasConstants;
import com.ravisravan.capstone.Constants.SPConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.UI.AllEventsFragment;
import com.ravisravan.capstone.UI.adapters.RemindersAdapter;
import com.ravisravan.capstone.UI.intro.AppIntroductionActivity;
import com.ravisravan.capstone.utils.SharedPreferenceUtils;

public class AllEventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , AllEventsFragment.Callback{//, LoaderManager.LoaderCallbacks<Cursor> {

    FloatingActionButton fab_add_reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri contentUri = getIntent() != null ? getIntent().getData() : null;
        setContentView(R.layout.activity_all_events);
        if (SharedPreferenceUtils.getInstance(this).getBooleanPreference(SPConstants.KEY_SHOW_APP_INTRO, true)) {
            Intent appIntroIntent = new Intent(AllEventsActivity.this, AppIntroductionActivity.class);
            startActivity(appIntroIntent);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialiseUIComponents();
        setOnClickListeners();
        AllEventsFragment allEventsFragment = ((AllEventsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_all_events));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (contentUri != null) {
//            allEventsFragment.setInitialSelectedReminder(
//                    WeatherContract.WeatherEntry.getDateFromUri(contentUri));
        }
    }

    private void initialiseUIComponents() {
        fab_add_reminder = (FloatingActionButton) findViewById(R.id.fab_add_reminder);
    }

    private void setOnClickListeners() {
        fab_add_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddReminderPage();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_message) {
            Intent viewMessagesIntent = new Intent(AllEventsActivity.this, MessagesActivity.class);
            startActivity(viewMessagesIntent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAddReminderPage() {
        Intent intent = new Intent(AllEventsActivity.this, AddReminderActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(Uri uri, RemindersAdapter.ReminderViewHolder viewHolder) {
//        if (mTwoPane) {
//            // In two-pane mode, show the detail view in this activity by
//            // adding or replacing the detail fragment using a
//            // fragment transaction.
//            Bundle args = new Bundle();
//            args.putParcelable(DetailActivityFragment.DETAIL_URI, contentUri);
//
//            DetailActivityFragment fragment = new DetailActivityFragment();
//            fragment.setArguments(args);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
//                    .commit();
//        } else {
//            Intent intent = new Intent(this, DetailActivity.class)
//                    .setData(contentUri);
//            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
//                    new Pair<View, String>(vh.mIconView, getString(R.string.detail_icon_transition_name)));
//
//            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
//        }
        Intent intent = new Intent(AllEventsActivity.this,ViewReminderActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }
}
