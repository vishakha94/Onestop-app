package com.example.android.onestop.app;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelp.clientlib.entities.Business;

import java.util.List;
import java.util.Locale;

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
        ImageView map_button; // button to open google map to navigate
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.list_item_yelpsearch, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.spot_name = (TextView) convertView.findViewById(R.id.title);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.map_button = (ImageView) convertView.findViewById(R.id.map_button);

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

        final double destLat = mBusinessList.get(position).location().coordinate().latitude();
        final double destLng = mBusinessList.get(position).location().coordinate().longitude();

        Location.distanceBetween(
                latitude,
                longitude,
                destLat,
                destLng,
                results);
        viewHolder.distance.setText(String.format(" %.4f miles", results[0]/ 1609.34));

        viewHolder.map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f", latitude, longitude, destLat, destLng);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mContext.startActivity(intent);
                Log.v("YelpSearch", "Open map activity");
            }
        });

        return convertView;
    }

}
