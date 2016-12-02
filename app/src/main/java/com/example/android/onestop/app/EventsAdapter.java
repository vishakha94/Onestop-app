package com.example.android.onestop.app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.api.client.util.DateTime;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Zixiao on 11/8/2016.
 */

public class EventsAdapter extends BaseAdapter {
    private Context mContext;
    private List<OneStopEvent> mEventsList;
    private static LayoutInflater mInflater = null;

    public EventsAdapter(Context c, List<OneStopEvent> eList) {
        // TODO Auto-generated constructor stub
        mEventsList = eList;
        mContext = c;
        mInflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return mEventsList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mEventsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder // Hole the view of items will be shown
    {
        TextView event_name; // name of the event
        TextView date;
        TextView datetime;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.list_item_original_event, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.event_name = (TextView) convertView.findViewById(R.id.list_item_event_name);
            viewHolder.date = (TextView) convertView.findViewById(R.id.list_item_date);
            viewHolder.datetime = (TextView) convertView.findViewById(R.id.list_item_date_time);

            convertView.setTag(viewHolder);
        } else {
            // Need to understand what does this line for
            viewHolder = (ViewHolder) convertView.getTag();
        }

        OneStopEvent e = mEventsList.get(position);
        viewHolder.event_name.setText(e.getEvent_name());

        if (e.getEnd_time().equals(OneStopEvent.ALL_DAY_EVENT)) {
            // all-day event
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat df_show = new SimpleDateFormat("MM dd, yyyy");
            try {
                viewHolder.date.setText(df_show.format(df.parse(e.getStart_time())));
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            viewHolder.datetime.setText("");
        } else {
            // not all-day event
            viewHolder.date.setText(formatDateFromstring("yyyy-MM-dd","MMM dd, yyyy", e.getStart_time().substring(0, 10)));
            String starttime = formatTimeFromString("hh:mm:ss","hh:mm a",e.getStart_time().substring(11,19));
            String endtime = formatTimeFromString("hh:mm:ss","hh:mm a",e.getEnd_time().substring(11,19));
            viewHolder.datetime.setText(starttime + "-" + endtime);

        }

        if (e.getSource().equals(OneStopEvent.SOURCE_FACEBOOK)) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.facebookcolorList));
        } else if (e.getSource().equals(OneStopEvent.SOURCE_GOOGLECALENDAR)) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.googlecolorList));
        }

        return convertView;
    }

    public static String formatDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat);
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat);

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (java.text.ParseException e) {
            Log.e("DateFormat", "ParseException - dateFormat");
        }

        return outputDate;

    }

    public static String formatTimeFromString(String inputFormat, String outputFormat, String inputTime){
        SimpleDateFormat t_input = new SimpleDateFormat(inputFormat);
        SimpleDateFormat t_output = new SimpleDateFormat(outputFormat);
        Date dt;
        String outputTime = null;

        try {
            dt = t_input.parse(inputTime);
            outputTime=t_output.format(dt);
            //System.out.println("Time Display: " + outputTime);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return outputTime;
    }


    public void clear () {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEventsList.clear();
                notifyDataSetChanged();
            }
        });
    }

    public void add (OneStopEvent e) {
        final OneStopEvent ee = e;
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEventsList.add(ee);
                notifyDataSetChanged();
            }
        });

    }
}
