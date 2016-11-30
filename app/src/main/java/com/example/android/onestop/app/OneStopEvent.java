package com.example.android.onestop.app;

import android.util.Log;

import com.google.api.services.calendar.model.Event;

/**
 * Created by Zixiao on 11/29/2016.
 */

public class OneStopEvent {
    private int id;
    private String event_name;
    private String source;
    private String start_time;
    private String end_time;
    private String category;
    private String spot_name;
    private String address;
    private Double latitude;
    private Double longitude;

    public static String TimeFormat = "";
    public static Double NONE_LOCATION = -1000.0;
    public static String SOURCE_GOOGLECALENDAR = "googlecalendar";
    public static String SOURCE_EVERNOTE = "evernote";
    public static String SOURCE_FACEBOOK = "facebook";

    public OneStopEvent() {
    }

    // Parameter e is defined in google calendar api
    public OneStopEvent(Event e) {
        String TAG = "EventCreate";
        this.event_name = e.getSummary();
        this.source = SOURCE_GOOGLECALENDAR;
        this.start_time = e.getStart().getDateTime().toString();
        Log.d(TAG, "start time:" + this.start_time);
        this.end_time = e.getEnd().getDateTime().toString();
        Log.d(TAG, "end time:" + this.end_time);
        // TODO: category analysis
        this.category = "";
        this.spot_name = e.getLocation();
        Log.d(TAG, "Location:" + this.spot_name);
        if (this.spot_name == null) {
            this.address = "";
            this.latitude = NONE_LOCATION;
            this.longitude = NONE_LOCATION;
            Log.d(TAG, "Location:" + this.spot_name);
        } else {
            // TODO: analyse location string
            this.address = "";
            this.latitude = NONE_LOCATION;
            this.longitude = NONE_LOCATION;
        }
    }

    public void print() {
        String output = "";
        output += this.event_name + " \n";
        output += this.source + " \n";
        output += this.start_time + " \n";
        output += this.end_time + " \n";
        output += this.category + " \n";
        output += this.spot_name + " \n";
        output += this.address + " \n";
        output += this.latitude.toString() + " \n";
        output += this.longitude.toString() + " \n";
        Log.d("++++++++++++++", output);
    }

    public OneStopEvent(String event_name, String source, String start_time, String end_time, String category, String spot_name, String address, Double latitude, Double longitude) {
        this.event_name = event_name;
        this.source = source;
        this.start_time = start_time;
        this.end_time = end_time;
        this.category = category;
        this.spot_name = spot_name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpot_name() {
        return spot_name;
    }

    public void setSpot_name(String spot_name) {
        this.spot_name = spot_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
