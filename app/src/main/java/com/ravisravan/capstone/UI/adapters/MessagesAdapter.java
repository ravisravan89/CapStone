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
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    final private Context mContext;
    final private MessagesAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mICM;
    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;
    private Cursor mCursor;
    private boolean returnResult;

    public MessagesAdapter(Context context, MessagesAdapterOnClickHandler dh, View emptyView, int choiceMode, boolean returnResult) {
        mContext = context;
        mClickHandler = dh;
        mEmptyView = emptyView;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
        this.returnResult = returnResult;
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messages_item, viewGroup, false);
            view.setFocusable(true);
            return new MessagesViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        int messageId = mCursor.getInt(mCursor.getColumnIndex(ReminderContract.MessageLkpTable._ID));
        String messageContent = mCursor.getString(mCursor.getColumnIndex(ReminderContract.MessageLkpTable.COLUMN_CONTENT));
        holder.tv_message_content.setText(messageContent);
        mICM.onBindViewHolder(holder, position);
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

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }

    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }

    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof MessagesViewHolder) {
            MessagesViewHolder vfh = (MessagesViewHolder) viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

    public static interface MessagesAdapterOnClickHandler {
        void onClick(Long id, MessagesViewHolder vh, String message);
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public class MessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_message_content;
        ImageView img_message_edit;
        ImageView img_message_delete;

        public MessagesViewHolder(View view) {
            super(view);
            tv_message_content = (TextView) view.findViewById(R.id.tv_message_content);
            img_message_edit = (ImageView) view.findViewById(R.id.img_message_edit);
            img_message_delete = (ImageView) view.findViewById(R.id.img_message_delete);
            img_message_edit.setOnClickListener(this);
            img_message_delete.setOnClickListener(this);
            //TODO: setonclickListener for view if called for onActivityResult
            if (returnResult)
                view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int idColumnIndex = mCursor.getColumnIndex(ReminderContract.MessageLkpTable._ID);
            int contentColIndex = mCursor.getColumnIndex(ReminderContract.MessageLkpTable.COLUMN_CONTENT);
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            mClickHandler.onClick(mCursor.getLong(idColumnIndex), this, mCursor.getString(contentColIndex));
            mICM.onClick(this);
            switch (v.getId()) {
                case R.id.img_message_delete:
                    showAlert(mCursor.getLong(idColumnIndex), mCursor.getString(contentColIndex), true);
                    break;
                case R.id.img_message_edit:
                    showAlert(mCursor.getLong(idColumnIndex), mCursor.getString(contentColIndex), false);
                    break;
            }
        }

        private void showAlert(final long id, final String message, final boolean delete) {
            final Activity mActivity = (Activity) mContext;
            final Dialog mDialog = new Dialog(mContext);
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View layout = inflater.inflate(R.layout.common_alert, null);
            TextView title = (TextView) layout.findViewById(R.id.title);
            String titleText = delete ? "Delete?" : "Edit?";
            title.setText(titleText);
            String okBtnText = delete ? "Delete" : "Edit";
            TextView msg = (TextView) layout.findViewById(R.id.msg);
            msg.setText(message);
            final EditText et_msg = (EditText) layout.findViewById(R.id.et_msg);
            et_msg.setVisibility(delete ? View.GONE : View.VISIBLE);
            et_msg.setText(message);
            et_msg.setSelection(message.length());
            msg.setVisibility(delete ? View.VISIBLE : View.GONE);
            View.OnClickListener ok_listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.cancel();
                    try {
                        if (delete) {
                            mContext.getContentResolver().delete(ReminderContract.MessageLkpTable.CONTENT_URI,
                                    ReminderContract.MessageLkpTable._ID + " = ?",
                                    new String[]{Long.toString(id)});
                        } else {
                            if (TextUtils.isEmpty(et_msg.getText())) {
                                et_msg.setError("Message is empty");
                            } else {
                                ContentValues cv = new ContentValues();
                                cv.put(ReminderContract.MessageLkpTable.COLUMN_CONTENT, et_msg.getText().toString());
                                mContext.getContentResolver().update(ReminderContract.MessageLkpTable.CONTENT_URI,
                                        cv, ReminderContract.MessageLkpTable._ID + " = ?",
                                        new String[]{Long.toString(id)});
                            }
                        }
                    } catch (SQLException e) {

                    } catch (UnsupportedOperationException e) {

                    }
                }
            };
            msg.setMovementMethod(new ScrollingMovementMethod());
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(layout);
            mDialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mDialog.show();
            Button okButton = (Button) layout.findViewById(R.id.btn_ok);
            okButton.setOnClickListener(ok_listener);
            Button cancelButton = (Button) layout.findViewById(R.id.btn_cancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDialog.cancel();
                }
            });
            okButton.setText(okBtnText);
        }
    }
}
