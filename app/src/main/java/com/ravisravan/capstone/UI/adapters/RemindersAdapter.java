package com.ravisravan.capstone.UI.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ravisravan.capstone.R;
import com.ravisravan.capstone.data.ReminderContract;
import com.ravisravan.capstone.utils.ItemChoiceManager;

/**
 * Created by ravisravankumar on 22/10/16.
 */
public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder> {

    final private Context mContext;
    final private View mEmptyView;
    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;
    private Cursor mCursor;
    private ReminderAdapterOnClickHandler mOnClickHandler;

    public RemindersAdapter(Context context, View emptyView, ReminderAdapterOnClickHandler reminderAdapterOnClickHandler) {
        mContext = context;
        mEmptyView = emptyView;
        mOnClickHandler = reminderAdapterOnClickHandler;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reminders_item, viewGroup, false);
            view.setFocusable(true);
            return new ReminderViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(ReminderViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int reminderId = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.Reminders._ID));
        String reminderTitle = mCursor.getString(mCursor.getColumnIndex(ReminderContract.Reminders.COLUMN_TITLE));
        String reminderDescription = mCursor.getString(mCursor.getColumnIndex(ReminderContract.Reminders.COLUMN_DESCRIPTION));
        holder.tv_reminder_title.setText(reminderTitle);
        holder.tv_reminder_description.setText(reminderDescription);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public Cursor getCursor() {
        return mCursor;
    }


    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ReminderViewHolder) {
            ReminderViewHolder vfh = (ReminderViewHolder) viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

    public static interface ReminderAdapterOnClickHandler {
        void onClick(Long id, ReminderViewHolder vh);
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_reminder_title;
        TextView tv_reminder_description;

        public ReminderViewHolder(View view) {
            super(view);
            tv_reminder_title = (TextView) view.findViewById(R.id.tv_reminder_title);
            tv_reminder_description = (TextView) view.findViewById(R.id.tv_reminder_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int idColumnIndex = mCursor.getColumnIndex(ReminderContract.Reminders._ID);
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            mOnClickHandler.onClick(mCursor.getLong(idColumnIndex), this);
        }
    }
}
