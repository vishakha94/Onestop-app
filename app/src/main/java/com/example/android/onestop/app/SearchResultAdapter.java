package com.example.android.onestop.app;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yelp.clientlib.entities.Business;

import java.util.List;

/**
 * Created by Zixiao on 10/27/2016.
 */

public class SearchResultAdapter extends BaseAdapter{

    private Context mContext;
    private List<Business> mBusinessList;
    private double latitude;
    private double longitude;

    private static LayoutInflater mInflater = null;
    public SearchResultAdapter(Context c, List<Business> businessList, double lat, double lng) {
        // TODO Auto-generated constructor stub
        mBusinessList = businessList;
        mContext = c;
        mInflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        latitude = lat;
        longitude = lng;
    }
    @Override
    public int getCount() {
        return mBusinessList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mBusinessList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder // Hole the view of items will be shown
    {
        TextView spot_name; // Name of location
        TextView distance; // distance from current location
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.list_item_yelpsearch, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.spot_name = (TextView) convertView.findViewById(R.id.title);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);

            convertView.setTag(viewHolder);
        } else {
            // Need to understand what does this line for
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.spot_name.setText(mBusinessList.get(position).name());
        float[] results = new float[1];
        Log.d("DISTANCE", mBusinessList.get(position).location().toString());
        com.yelp.clientlib.entities.Location loc = mBusinessList.get(position).location();
        if (loc == null) {
            viewHolder.distance.setText("");
            return convertView;
        }

        if (loc.coordinate() == null) {
            viewHolder.distance.setText("");
            return convertView;
        }

        Location.distanceBetween(
                latitude,
                longitude,
                mBusinessList.get(position).location().coordinate().latitude(),
                mBusinessList.get(position).location().coordinate().longitude(),
                results);
        viewHolder.distance.setText(String.format(" %.4f miles", results[0]/ 1609.34));
        return convertView;
    }

}
