/*
 * Copyright (c) 2015-2017 by k3b.
 */

package de.k3b.android.widget;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import de.k3b.io.ListUtils;

/**
 * Add history-popup to EditText.
 * invoke via Clipboard ContextActionBar: star (history) to open a popupmenu with previous values.
 * Long-Press if no selection => popup with previous values.
 * <p>
 * Popup-menu requires at least api 11 (HONEYCOMB)
 * Created by k3b on 26.08.2015.
 */
public class HistoryEditText {
    private static final int NO_ID = -1;
    private final Activity mContext;
    private final String mDelimiter;
    private final int mMaxHisotrySize;
    private final EditorHandler[] mEditorHandlers;

    /**
     * ContextActionBar for one EditText
     */
    protected class EditorHandler implements View.OnLongClickListener, View.OnClickListener {
        private final EditText mEditor;
        private final ImageButton mCmd;
        private final String mId;

        public EditorHandler(String id, EditText editor, int cmdId) {
            mId = id;
            mEditor = editor;

            mCmd = (cmdId != NO_ID) ? (ImageButton) mContext.findViewById(cmdId) : null;

            if (mCmd == null) {
                mEditor.setOnLongClickListener(this);
            } else {
                mCmd.setOnClickListener(this);
                mCmd.setVisibility(View.VISIBLE);
            }
        }

        public String toString(SharedPreferences pref) {
            return mId + " : '" + getHistory(pref) + "'";
        }

        protected void showHistory() {
            List<String> items = getHistoryItems();

            PopupMenu popup = new PopupMenu(mContext, mEditor);
            Menu root = popup.getMenu();
            int len = items.size();
            if (len > 10) len = 10;
            for (int i = 0; i < len; i++) {
                String text = items.get(i).trim();

                if ((text != null) && (text.length() > 0)) {
                    root.add(Menu.NONE, i + 10, Menu.NONE, getCondensed(text));
                }
            }

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // String text = item.getTitle();
                    String text = getHistoryItems().get(item.getItemId() - 10);
                    mEditor.setText(text);
                    mEditor.setSelection(0, mEditor.length());
                    return true;
                }
            });
            popup.show();
        }

        @NonNull
        private List<String> getHistoryItems() {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            return getHistory(sharedPref);
        }

        private CharSequence getCondensed(String text) {
            int tlen = text.length();
            if (tlen > 32) {
                return text.substring(0, 5) + "..." + text.subSequence(tlen - 29, tlen);

            }
            return text;
        }

        private List<String> getHistory(SharedPreferences sharedPref) {
            String history = sharedPref.getString(mId, "");
            return asList(history);
        }

        protected void saveHistory(SharedPreferences sharedPref, SharedPreferences.Editor edit) {
            List<String> history = getHistory(sharedPref);
            history = include(history, mEditor.getText().toString().trim());
            String result = toString(history);
            edit.putString(mId, result);
        }

        private List<String> asList(String serialistedListElements) {
            String[] items = serialistedListElements.split(mDelimiter);
            return Arrays.asList(items);
        }

        private String toString(List<String> list) {
            return ListUtils.toString(list, mDelimiter);
        }

        private List<String> include(List<String> history_, String newValue) {
            List<String> history = new ArrayList<>(history_);
            if ((newValue != null) && (newValue.length() > 0)) {
                history.remove(newValue);
                history.add(0, newValue);
            }

            int len = history.size();

            // forget oldest entries if maxHisotrySize is reached
            while (len > mMaxHisotrySize) {
                len--;
                history.remove(len);
            }
            return history;
        }

        @Override
        public void onClick(View v) {
            showHistory();
        }

        @Override
        public boolean onLongClick(View v) {

            if (mEditor.getSelectionEnd() - mEditor.getSelectionStart() > 0) {
                return false;
            }
            showHistory();
            return true;
        }
    }

    /**
     * define history function for these editors
     */
    public HistoryEditText(Activity context, int[] cmdIds, EditText... editors) {
        this(context, context.getClass().getSimpleName() + "_history_", "';'", 8, cmdIds, editors);
    }

    /**
     * define history function for these editors
     */
    public HistoryEditText(Activity context, String settingsPrefix, String delimiter, int maxHisotrySize, int[] cmdIds, EditText... editors) {

        this.mContext = context;
        this.mDelimiter = delimiter;
        this.mMaxHisotrySize = maxHisotrySize;
        mEditorHandlers = new EditorHandler[editors.length];

        for (int i = 0; i < editors.length; i++) {
            mEditorHandlers[i] = createHandler(settingsPrefix + i, editors[i], getId(cmdIds, i));
        }
    }

    protected EditorHandler createHandler(String id, EditText editor, int cmdId) {
        return new EditorHandler(id, editor, cmdId);
    }

    private int getId(int[] ids, int offset) {
        if ((ids != null) && (offset >= 0) && (offset < ids.length)) return ids[offset];
        return NO_ID;
    }

    /**
     * include current editor-content to history and save to settings
     */
    public void saveHistory() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor edit = sharedPref.edit();

        for (EditorHandler instance : mEditorHandlers) {
            instance.saveHistory(sharedPref, edit);
        }
        edit.apply();
    }

    @NonNull
    @Override
    public String toString() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        StringBuilder result = new StringBuilder();
        for (EditorHandler instance : mEditorHandlers) {
            result.append(instance.toString(sharedPref)).append("\n");
        }
        return result.toString();
    }
}
