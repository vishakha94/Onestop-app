package com.example.android.onestop.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Zixiao on 11/29/2016.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> event_category; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> categoryToEventsMap;

    public ExpandableListAdapter(Context context, List<String> categories,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this.event_category = categories;
        this.categoryToEventsMap = listChildData;
    }

    @Override
    public int getGroupCount() {
        return this.event_category.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.categoryToEventsMap.get(this.event_category.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.event_category.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.categoryToEventsMap.get(this.event_category.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String categoryName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_categories, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.list_item_categories_textview);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(categoryName);

        ImageView locateButton = (ImageView) convertView.findViewById(R.id.list_item_categories_locatebutton);
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start yelp search activity
                Intent yelpSearchIntent = new Intent(_context, YelpSearchActivity.class);
                _context.startActivity(yelpSearchIntent);
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String eventName = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_event_category, null);
        }

        TextView categoryTextView = (TextView) convertView
                .findViewById(R.id.exp_list_event);

        categoryTextView.setText(eventName);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
