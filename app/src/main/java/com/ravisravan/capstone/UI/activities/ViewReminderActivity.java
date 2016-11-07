package com.ravisravan.capstone.UI.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ravisravan.capstone.Constants.Constants;
import com.ravisravan.capstone.Constants.ExtrasConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.UI.ViewReminderFragment;

public class ViewReminderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reminder);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable(ExtrasConstants.REMINDER_URI, getIntent().getData());
            //TODO: add transitions later
            //arguments.putBoolean(DetailActivityFragment.DETAIL_TRANSITION_ANIMATION, true);

            ViewReminderFragment fragment = new ViewReminderFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.reminder_detail_container, fragment)
                    .commit();

            //Being here means we are in animation mode
            //supportPostponeEnterTransition();
        }
    }

}
