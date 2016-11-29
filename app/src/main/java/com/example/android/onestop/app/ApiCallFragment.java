
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

        if (id == R.id.action_refresh) {

            updateData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.
        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_forecast, // The name of the layout ID.
                        R.id.list_item_forecast_textview, // The ID of the textview to populate.
                        new ArrayList<String>());


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mForecastAdapter.getItem(position);
                //Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();

                Intent textDetail = new Intent(getActivity(), DetailActivity.class);
                textDetail.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(textDetail);


            }
        });


        return rootView;
    }

    private void updateData() {
        CallApiTask apiTask = new CallApiTask();

        //get prefernce from settings page
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String location = prefs.getString(getString(R.string.pref_location_key),
        //        getString(R.string.pref_location_default));
        //Repeat the above line and get boolean value
        //pref_array=Add these boolean values in an array
        String permission = "true";
        /// /apiTask.execute(pref_array);
        apiTask.execute(permission);
    }


    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }


    public class CallApiTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = CallApiTask.class.getSimpleName();


        //Implement Helper functions for JSON manipulation

        private String[] getEventsDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String FGA_DATA = "data";
            final String FGA_NAME = "name";
            final String FGA_STIME = "start_time";
            final String FGA_ETIME = "end_time";



            JSONObject facebookJson = new JSONObject(forecastJsonStr);
            JSONArray facebookArray = facebookJson.getJSONArray(FGA_DATA);

          
            Log.v(LOG_TAG,"Length of facebook array="+facebookArray.length());
            String[] resultStrs = new String[3];
            for (int i = 0; i < 3; i++) {

                // Get the JSON object representing the event
                JSONObject dataElement = facebookArray.getJSONObject(i);
                String dataName = dataElement.getString(FGA_NAME);
                String dataStartTime = dataElement.getString(FGA_STIME).substring(11,16);
                String dataEndTime = dataElement.getString(FGA_ETIME).substring(11,16);


                Log.v(LOG_TAG,"Facebook data array"+" "+dataName+" "+dataStartTime+" "+dataEndTime);

                resultStrs[i] = dataName+" "+dataStartTime+" "+dataEndTime;

            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Facebook data entry: " + s);
            }

            return resultStrs;

        }


        //**********************************************
        @Override
        protected String[] doInBackground(String... params) {


            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }


            if(params[0]=="true"){


                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String forecastJsonStr = null;




                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast
                    final String FORECAST_BASE_URL =
                            "https://graph.facebook.com/v2.8/794961210523337/events?";
                    final String APPID_PARAM = "access_token";

                    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendQueryParameter(APPID_PARAM, "EAACEdEose0cBANGiX3iCUglvNhWWhZBV6sbY0ACF4nPlZBZAAzZCZBWv2oO0ej0EU9ZAY0RwDZBjiismhEh7ajlbfEZAVMji00xxpFvNoGLQeJmZCmyOdoqnFQxiTwRNe0gYnfaTzWYAfgtW68sI9ra6JdyRDW6Kpm3Nnu7GLofdTVk7ZADoMLTdpZCFPYghGrx7iUZD")
                            .build();

                    URL url = new URL(builtUri.toString());

                    Log.v(LOG_TAG, "Built URI " + builtUri.toString());


                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }

                    forecastJsonStr = buffer.toString();
                    Log.v(LOG_TAG, "Facebook JSON String:" + forecastJsonStr);

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }
                try {
                    return getEventsDataFromJson(forecastJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }



            }


            return null;
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
}


