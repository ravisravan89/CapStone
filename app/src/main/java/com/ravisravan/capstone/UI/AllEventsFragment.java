package com.ravisravan.capstone.UI;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.ravisravan.capstone.R;
import com.ravisravan.capstone.UI.adapters.RemindersAdapter;
import com.ravisravan.capstone.data.ReminderContract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllEventsFragment.Callback} interface
 * to handle interaction events.
 */
public class AllEventsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int REMINDERS_LOADER = 200;
    private RecyclerView recyclerview_reminders;
    private RemindersAdapter mRemindersAdapter;
    private Callback mListener;

    public AllEventsFragment() {
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
        View rootview = inflater.inflate(R.layout.fragment_all_events, container, false);
        recyclerview_reminders = (RecyclerView) rootview.findViewById(R.id.recyclerview_reminders);

        //TODO: configure the spancount in integers and use
        recyclerview_reminders.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        View emptyView = rootview.findViewById(R.id.recyclerview_reminders_empty);
        mRemindersAdapter = new RemindersAdapter(getActivity(), emptyView,
                new RemindersAdapter.ReminderAdapterOnClickHandler() {
                    @Override
                    public void onClick(Long id, RemindersAdapter.ReminderViewHolder vh) {
                        ((Callback) getActivity())
                                .onItemSelected(ReminderContract.Reminders.buildReminderUri(id), vh);
                    }
                });
        recyclerview_reminders.setAdapter(mRemindersAdapter);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerview_reminders.setHasFixedSize(true);
        return rootview;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // we hold for transition here just in case the activity
        // needs to be recreated. In a standard return transition,
        // this doesn't actually make a difference.
        //TODO: transitions
//        if (mHoldForTransition) {
//            getActivity().supportPostponeEnterTransition();
//        }
        getLoaderManager().initLoader(REMINDERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mListener = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //1 is active 0 is inactive
        return new CursorLoader(getActivity(), ReminderContract.Reminders.CONTENT_URI, null,
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
//                    if (null != vh) {
//                        mRemindersAdapter.selectView(vh);
//                    }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface Callback {
        /**
         * ViewReminder for when an item has been selected.
         */
        public void onItemSelected(Uri uri, RemindersAdapter.ReminderViewHolder vh);
    }
}
