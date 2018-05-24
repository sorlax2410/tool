package com.kenshi.Plugins.MITM_GUI;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.kenshi.Core.System;
import com.kenshi.Plugins.mitm.SpoofSession;
import com.mitdroid.kenshi.Main.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PasswordSnifferActivity extends SherlockActivity {

    public class ListViewAdapter extends BaseExpandableListAdapter {

        private HashMap<String, ArrayList<String>> groups = null;
        private Context context;

        public ListViewAdapter(Context context) {
            groups = new HashMap<>();
            this.context = context;
        }

        public boolean children(String name) { return groups.containsKey(name); }

        public void addGroup(String name) {
            groups.put(name, new ArrayList<String>());
            notifyDataSetChanged();
        }

        public boolean hasChild(String group, String line) {
            ArrayList<String>children = groups.get(group);

            if(!children.isEmpty())
                for(String child : children)
                    if(child.equals(line))
                        return true;

            return false;
        }

        public synchronized void addChild(String group, String child) {
            if(groups.get(group).isEmpty())
                addGroup(group);
            groups.get(group).add(child);

            Object[]keys = groups.keySet().toArray();
            int groups = keys.length;

            for(int index = 0; index < groups; index++) {
                if (keys[index].toString().equals(group)) {
                    listView.expandGroup(index);
                    break;
                }
            }
            notifyDataSetChanged();
        }

        private ArrayList<String>getGroupAt(int position) { return groups.get(groups.keySet().toArray()[position]); }


        /**
         * Gets the number of groups.
         *
         * @return the number of groups
         */
        @Override
        public int getGroupCount() {
            return groups.size();
        }

        /**
         * Gets the number of children in a specified group.
         *
         * @param groupPosition the position of the group for which the children
         *                      count should be returned
         * @return the children count in the specified group
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            return getGroupAt(groupPosition).size();
        }

        /**
         * Gets the data associated with the given group.
         *
         * @param groupPosition the position of the group
         * @return the data child for the specified group
         */
        @Override
        public Object getGroup(int groupPosition) {
            return groups.keySet().toArray()[groupPosition];
        }

        /**
         * Gets the data associated with the given child within the given group.
         *
         * @param groupPosition the position of the group that the child resides in
         * @param childPosition the position of the child with respect to other
         *                      children in the group
         * @return the data of the child
         */
        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return getGroupAt(groupPosition).get(childPosition);
        }

        /**
         * Gets the ID for the group at the given position. This group ID must be
         * unique across groups. The combined ID (see
         * {@link #getCombinedGroupId(long)}) must be unique across ALL items
         * (groups and all children).
         *
         * @param groupPosition the position of the group for which the ID is wanted
         * @return the ID associated with the group
         */
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        /**
         * Gets the ID for the given child within the given group. This ID must be
         * unique across all children within the group. The combined ID (see
         * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
         * (groups and all children).
         *
         * @param groupPosition the position of the group that contains the child
         * @param childPosition the position of the child within the group for which
         *                      the ID is wanted
         * @return the ID associated with the child
         */
        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return (groupPosition*10) + childPosition;
        }

        /**
         * Indicates whether the child and group IDs are stable across changes to the
         * underlying data.
         *
         * @return whether or not the same ID always refers to the same object
         * @see Adapter#hasStableIds()
         */
        @Override
        public boolean hasStableIds() { return true; }

        /**
         * Gets a View that displays the given group. This View is only for the
         * group--the Views for the group's children will be fetched using
         * {@link #getChildView(int, int, boolean, View, ViewGroup)}.
         *
         * @param groupPosition the position of the group for which the View is
         *                      returned
         * @param isExpanded    whether the group is expanded or collapsed
         * @param convertView   the old view to reuse, if possible. You should check
         *                      that this view is non-null and of an appropriate type before
         *                      using. If it is not possible to convert this view to display
         *                      the correct data, this method can create a new view. It is not
         *                      guaranteed that the convertView will have been previously
         *                      created by
         *                      {@link #getGroupView(int, boolean, View, ViewGroup)}.
         * @param parent        the parent that this view will eventually be attached to
         * @return the View corresponding to the group at the specified position
         */
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent)
        {
            TextView row = (TextView)convertView;

            if(row == null)
                row = new TextView(context);

            row.setText(getGroup(groupPosition).toString());
            row.setTextSize(20);
            row.setTypeface(Typeface.DEFAULT_BOLD);
            row.setPadding(50, 0, 0, 0);
            return row;
        }

        /**
         * Gets a View that displays the data for the given child within the given
         * group.
         *
         * @param groupPosition the position of the group that contains the child
         * @param childPosition the position of the child (for which the View is
         *                      returned) within the group
         * @param isLastChild   Whether the child is the last child within the group
         * @param convertView   the old view to reuse, if possible. You should check
         *                      that this view is non-null and of an appropriate type before
         *                      using. If it is not possible to convert this view to display
         *                      the correct data, this method can create a new view. It is not
         *                      guaranteed that the convertView will have been previously
         *                      created by
         *                      {@link #getChildView(int, int, boolean, View, ViewGroup)}.
         * @param parent        the parent that this view will eventually be attached to
         * @return the View corresponding to the child at the specified position
         */
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent)
        {
            TextView row = (TextView)convertView;

            if(row == null)
                row = new TextView(context);

            row.setText(getChild(groupPosition, childPosition).toString());
            row.setPadding(30, 0, 0, 0);

            return row;
        }

        /**
         * Whether the child at the specified position is selectable.
         *
         * @param groupPosition the position of the group that contains the child
         * @param childPosition the position of the child within the group
         * @return whether the child is selectable.
         */
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
    }

    private static final String tag = "PASSWORD SNIFFER";

    private ToggleButton sniffToggleButton = null;
    private ProgressBar sniffProgress = null;
    private ExpandableListView listView = null;
    private ListViewAdapter listViewAdapter = null;
    private boolean running = false;
    private String fileOutput = null;
    private FileWriter fileWriter = null;
    private BufferedWriter bufferedWriter = null;
    private SpoofSession spoofSession = null;

    private void setStopState() {
        spoofSession.stop();

        try {
            if(bufferedWriter != null)
                bufferedWriter.close();
        }catch (IOException e) { System.errorLogging(tag, e); }

        sniffProgress.setVisibility(View.INVISIBLE);
        running = false;
        sniffToggleButton.setChecked(false);
    }

    private void setStartState() {
        try {
            fileWriter = new FileWriter(fileOutput, true);
            bufferedWriter = new BufferedWriter(fileWriter);
        }catch (IOException e) {  }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(System.getCurrentTarget() + " > MITM > Password Sniffer");
        setContentView(R.layout.activity_password_sniffer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fileOutput = (new File(System.getStoragePath(),
                System.getSettings().getString("PREF_PASSWORD_FILENAME",
                        "MitDroid"))
                .getAbsolutePath()
        );
        sniffToggleButton = findViewById(R.id.sniffToggleButton);
        sniffProgress = findViewById(R.id.sniffProgress);
        listView = findViewById(R.id.expandableListView);
        listViewAdapter = new ListViewAdapter(this);
        spoofSession = new SpoofSession(false, false,
                null, null
        );
        listView.setAdapter(listViewAdapter);
        sniffToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(running)
                    setStopState();
                else
                    setStartState();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
