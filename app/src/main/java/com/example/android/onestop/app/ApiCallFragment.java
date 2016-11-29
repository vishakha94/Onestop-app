
package com.example.android.onestop.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.text.format.Time;
//import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.SimpleDateFormat;


/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ApiCallFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    public ApiCallFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*
        if (id == R.id.action_refresh) {

            updateData();

            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        String[] data = {
                "Principles UI Software - CS6455",
                "Realtimes System CS 6235",
                "UI Design & Eval CS 6455",
                "Veteran's Day",
                "Thanksgiving Day",
                "Project Submission CS 6235",
                "Christmas"
        };
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        weekForecast);

       

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mForecastAdapter.getItem(position);
                //Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();

                Intent textDetail = new Intent(getActivity(),DetailActivity.class);
                textDetail.putExtra(Intent.EXTRA_TEXT,forecast);
                startActivity(textDetail);



            }
        });


        return rootView;
    }
/*
    private void updateData() {
        CallApiTask apiTask = new CallApiTask();

        //get prefernce from settings page
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        //Repeat the above line and get boolean value
        //pref_array=Add these boolean values in an array

        /// /apiTask.execute(pref_array);
        apiTask.execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }

    public class CallApiTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = CallApiTask.class.getSimpleName();


        //Implement Helper functions for JSON manupulation


        //**********************************************
        @Override
        protected String[] doInBackground(int[]... params) {


            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }


            //unpack the pref_array to see which APIs to call
            //Suppose 1 1 0: Call GMAIL FACEBOOK EVERNOTE

            //if pref_array[0] is true call gmailAPI()
            //try catch block
            //do an api call
            //get json
            //use helper functions to read it if required
            //store in DB
            //pass to adapter to be populated through onpostexecute
            //if pref_array[1] is true call facebookAPI()
            // if pref_array[2] is true call evernoteAPI()



        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }

    */
}