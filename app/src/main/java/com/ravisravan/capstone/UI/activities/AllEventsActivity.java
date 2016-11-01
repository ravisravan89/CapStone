package com.ravisravan.capstone.UI.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.ravisravan.capstone.Constants.ExtrasConstants;
import com.ravisravan.capstone.Constants.SPConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.UI.adapters.MessagesAdapter;
import com.ravisravan.capstone.UI.adapters.RemindersAdapter;
import com.ravisravan.capstone.UI.intro.AppIntroductionActivity;
import com.ravisravan.capstone.data.ReminderContract;
import com.ravisravan.capstone.utils.SharedPreferenceUtils;

public class AllEventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    FloatingActionButton fab_add_reminder;
    private final int REMINDERS_LOADER = 200;
    private RecyclerView recyclerview_reminders;
    private RemindersAdapter mRemindersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);
        if (SharedPreferenceUtils.getInstance(this).getBooleanPreference(SPConstants.KEY_SHOW_APP_INTRO, true)) {
            Intent appIntroIntent = new Intent(AllEventsActivity.this, AppIntroductionActivity.class);
            startActivity(appIntroIntent);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initialiseUIComponents();
        setOnClickListeners();
        getSupportLoaderManager().initLoader(REMINDERS_LOADER, null, this);

        //TODO: configure the spancount in integers and use
        recyclerview_reminders.setLayoutManager(new GridLayoutManager(this, 1));
        View emptyView = findViewById(R.id.recyclerview_reminders_empty);
        mRemindersAdapter = new RemindersAdapter(this, emptyView);

        recyclerview_reminders.setAdapter(mRemindersAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerview_reminders.setHasFixedSize(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initialiseUIComponents() {
        fab_add_reminder = (FloatingActionButton) findViewById(R.id.fab_add_reminder);
        recyclerview_reminders = (RecyclerView) findViewById(R.id.recyclerview_reminders);
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
        intent.putExtra(ExtrasConstants.REMINDER_ID, -1L);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //1 is active 0 is inactive
        return new CursorLoader(this, ReminderContract.Reminders.CONTENT_URI, null,
                ReminderContract.Reminders.COLUMN_STATE + " = ?", new String[]{"1"},
                ReminderContract.Reminders.COLUMN_CREATED_DATE);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRemindersAdapter.swapCursor(data);
        recyclerview_reminders.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Since we know we're going to get items, we keep the listener around until
                // we see Children.
                recyclerview_reminders.getViewTreeObserver().removeOnPreDrawListener(this);
                if (recyclerview_reminders.getChildCount() > 0) {
                    int position = 0;
//                    if (position == RecyclerView.NO_POSITION &&
//                            -1 != mInitialSelectedMessage) {
//                        Cursor data = mMessagesAdapter.getCursor();
//                        int count = data.getCount();
//                        int messageColumn = data.getColumnIndex(ReminderContract.MessageLkpTable._ID);
//                        for (int i = 0; i < count; i++) {
//                            data.moveToPosition(i);
//                            if (data.getLong(messageColumn) == mInitialSelectedMessage) {
//                                position = i;
//                                break;
//                            }
//                        }
//                    }
                    //if (position == RecyclerView.NO_POSITION) position = 0;
                    // If we don't need to restart the loader, and there's a desired position to restore
                    // to, do so now.
                    recyclerview_reminders.smoothScrollToPosition(position);
                    RecyclerView.ViewHolder vh = recyclerview_reminders.findViewHolderForAdapterPosition(position);
                    if (null != vh) {
                        mRemindersAdapter.selectView(vh);
                    }
                    return true;
                } else {

                }
                return false;
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRemindersAdapter.swapCursor(null);
    }
}
