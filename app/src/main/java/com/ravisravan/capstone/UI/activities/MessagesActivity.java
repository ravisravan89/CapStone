package com.ravisravan.capstone.UI.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.EditText;

import com.ravisravan.capstone.Constants.ExtrasConstants;
import com.ravisravan.capstone.R;
import com.ravisravan.capstone.UI.adapters.MessagesAdapter;
import com.ravisravan.capstone.data.ReminderContract;

public class MessagesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText et_message_text;
    private static final int MESSAGE_LOADER = 100;
    private MessagesAdapter mMessagesAdapter;
    private RecyclerView recyclerview_messages;
    private int mChoiceMode;
    private int mPosition = RecyclerView.NO_POSITION;
    private boolean mAutoSelectView;
    private long mInitialSelectedMessage = -1;
    private static final String SELECTED_KEY = "selected_position";
    private boolean isNewMessageAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        setTitle(getString(R.string.messages_title));
        et_message_text = (EditText) findViewById(R.id.et_message_text);
        recyclerview_messages = (RecyclerView) findViewById(R.id.recyclerview_messages);
        getSupportLoaderManager().initLoader(MESSAGE_LOADER, null, this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        if (getIntent().hasExtra(ExtrasConstants.SELECT_MESSAGE)) {
//            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//        }
        // Set the layout manager
        //TODO: configure the spancount in integers and use
        recyclerview_messages.setLayoutManager(new GridLayoutManager(this, 1));
        View emptyView = findViewById(R.id.recyclerview_messages_empty);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerview_messages.setHasFixedSize(true);

        // The ForecastAdapter will take data from a source and
        // use it to populate the RecyclerView it's attached to.
        mMessagesAdapter = new MessagesAdapter(this, new MessagesAdapter.MessagesAdapterOnClickHandler() {
            @Override
            public void onClick(Long id, MessagesAdapter.MessagesViewHolder vh, String message) {
                //setInitialSelectedMessage(id);
//                Toast.makeText(getApplicationContext(), "" + id, Toast.LENGTH_LONG).show();
                mPosition = vh.getAdapterPosition();
                if (getIntent().hasExtra(ExtrasConstants.SELECT_MESSAGE)) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(ExtrasConstants.SELECTED_MESSAGE, message);
                    returnIntent.putExtra(ExtrasConstants.SELECTED_MESSAGE_ID, id);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        }, emptyView, mChoiceMode, getIntent().hasExtra(ExtrasConstants.SELECT_MESSAGE));
        recyclerview_messages.setAdapter(mMessagesAdapter);

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_KEY)) {
                // The RecyclerView probably hasn't even been populated yet.  Actually perform the
                // swapout in onLoadFinished.
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }
            mMessagesAdapter.onRestoreInstanceState(savedInstanceState);
        }
    }


    public void saveMessage(View view) {
        String message = et_message_text.getText().toString();
        if (TextUtils.isEmpty(message)) {
            return;
        } else {
            insertMessageInDb(message);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to RecyclerView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        mMessagesAdapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    public void setInitialSelectedMessage(long initialSelectedMessage) {
        mInitialSelectedMessage = initialSelectedMessage;
    }

    private void insertMessageInDb(String message) {
        long messageId;
        // Now that the content provider is set up, inserting rows of data is pretty simple.
        // First create a ContentValues object to hold the data you want to insert.
        ContentValues messagesValues = new ContentValues();
        // Then add the data, along with the corresponding name of the data type,
        // so the content provider knows what kind of value is being inserted.
        messagesValues.put(ReminderContract.MessageLkpTable.COLUMN_CONTENT, message);
        // Finally, insert location data into the database.
        Exception exception = null;
        try {
            Uri insertedUri = getContentResolver().insert(ReminderContract.MessageLkpTable.CONTENT_URI
                    , messagesValues);
            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            messageId = ContentUris.parseId(insertedUri);
            if (messageId > 0) {
                et_message_text.setText("");
                isNewMessageAdded = true;
                //recyclerview_messages.smoothScrollToPosition();
            }
        } catch (SQLException e) {
            exception = e;
        } catch (UnsupportedOperationException e) {
            exception = e;
        }
        if (exception != null) {
            if (exception instanceof SQLException) {
                Snackbar.make(findViewById(android.R.id.content),
                        "The message already exists.", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        "There was an error performing the action.", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TypedArray a = obtainStyledAttributes(R.styleable.MessagesActivity);
        mChoiceMode = a.getInt(R.styleable.MessagesActivity_android_choiceMode, AbsListView.CHOICE_MODE_NONE);
        mAutoSelectView = a.getBoolean(R.styleable.MessagesActivity_autoSelectView, false);
        a.recycle();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //getContentResolver().query(ReminderContract.MessageLkpTable.CONTENT_URI, null, null, null, null);
        return new CursorLoader(this, ReminderContract.MessageLkpTable.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMessagesAdapter.swapCursor(data);
        recyclerview_messages.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                // Since we know we're going to get items, we keep the listener around until
                // we see Children.
                recyclerview_messages.getViewTreeObserver().removeOnPreDrawListener(this);
                if (recyclerview_messages.getChildCount() > 0) {

                    int position = 0;
                    if (isNewMessageAdded) {
                        isNewMessageAdded = false;
                        Cursor data = mMessagesAdapter.getCursor();
                        position = data.getCount() - 1;
                    }
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
                    recyclerview_messages.smoothScrollToPosition(position);
                    RecyclerView.ViewHolder vh = recyclerview_messages.findViewHolderForAdapterPosition(position);
                    if (null != vh && mAutoSelectView) {
                        mMessagesAdapter.selectView(vh);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMessagesAdapter.swapCursor(null);
    }
}
