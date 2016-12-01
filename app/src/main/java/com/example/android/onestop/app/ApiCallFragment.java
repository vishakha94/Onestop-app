
//Google has a pretty good service for helper functions like settimenow etc
//For other services, writing a manual one :(

package com.example.android.onestop.app;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
//import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

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
import java.util.Date;
import java.util.IllegalFormatPrecisionException;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;


/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ApiCallFragment extends Fragment
        implements EasyPermissions.PermissionCallbacks{

    static int eventLimit=12;
    private ArrayAdapter<String> mEventNameAdapter;
    //private final String LOG_TAG = ApiCallFragment.class.getSimpleName();

    GoogleAccountCredential mCredential;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private ProgressDialog mProgress;

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
        // Google calendar -----------------------------------------------------
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Calling Google Calendar API ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        // ---------------------------------------------------------------------

        // The ArrayAdapter will take data from a source  and
        // use it to populate the ListView it's attached to.
        mEventNameAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.row,
                        R.id.list_item_name,
                        new ArrayList<String>());



        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listViewMain = (ListView) rootView.findViewById(R.id.listview_forecast);

        listViewMain.setAdapter(mEventNameAdapter);


        listViewMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mEventNameAdapter.getItem(position);
                //Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();

                Intent textDetail = new Intent(getActivity(), DetailActivity.class);
                textDetail.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(textDetail);


            }
        });


        return rootView;
    }

    private void updateData() {
        mEventNameAdapter.clear();
        CallApiTask apiTask = new CallApiTask();

        //get preference from settings page
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String location = prefs.getString(getString(R.string.pref_location_key),
        //        getString(R.string.pref_location_default));
        //Repeat the above line and get boolean value
        //pref_array=Add these boolean values in an array
        String permission = "true";
        /// /apiTask.execute(pref_array);
        apiTask.execute(permission);

        getResultsFromApi();
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


    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }


    public class CallApiTask extends AsyncTask<String, Void, String[][]> {


        private final String LOG_TAG = CallApiTask.class.getSimpleName();


        //Implement Helper functions for JSON manipulation

        private String[][] getEventsDataFromJson(String accountJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String FGA_DATA = "data";
            final String FGA_NAME = "name";
            final String FGA_STIME = "start_time";
            final String FGA_ETIME = "end_time";
            final String FGA_LOCATION = "location";
            final String FGA_CITY = "city";
            final String FGA_COUNTRY = "country";
            final String FGA_LAT = "latitude";
            final String FGA_LONG = "longitude";
            final String FGA_STATE = "state";
            final String FGA_STREET = "street";
            final String FGA_ZIP = "zip";
            String dataName;
            String dataSDate;
            String dataEDate;
            String dataLocation;
            String dataStartTime;
            String dataEndTime;
            String dataTimeStampStart;
            String dataTimeStampEnd;

            JSONObject dataJson = new JSONObject(accountJsonStr);
            JSONArray dataArray = dataJson.getJSONArray(FGA_DATA);


            //Limit to 5
            int lengthDataArray=dataArray.length();
            if (lengthDataArray>eventLimit) lengthDataArray=eventLimit;
            String[][] eventDetails = new String[lengthDataArray][3];
            for (int i = 0; i < lengthDataArray; i++) {

                    try {
                        // Get the JSON object representing the event
                        JSONObject dataElement = dataArray.getJSONObject(i);

                        //Check if fields exist, else set to default
                        if(dataElement.has(FGA_NAME)) {
                            dataName = dataElement.getString(FGA_NAME);

                        }else dataName="No event name";


                        if(dataElement.has(FGA_STIME)) {
                            dataTimeStampStart = dataElement.getString(FGA_STIME);
                            dataSDate=formatDateFromstring("yyyy-MM-dd","MMM dd, yyyy",dataTimeStampStart.substring(0,10));

                            dataStartTime = formatTimeFromString("hh:mm:ss","hh:mm a",dataTimeStampStart.substring(11,19));

                            if(dataElement.has(FGA_ETIME)) {
                                dataTimeStampEnd = dataElement.getString(FGA_ETIME);
                                //dataEDate=formatDateFromstring("hh:mm:ss","hh:mm a",dataTimeStampEnd.substring(0,10));

                                dataEndTime = formatTimeFromString("hh:mm:ss","hh:mm a",dataElement.getString(FGA_ETIME).substring(11,19));

                            } else dataEndTime = "12:00 AM";

                        } else {
                                dataSDate="All Day";
                                dataStartTime = "";
                                dataEndTime="";}

                        //Get time proper format

                        eventDetails[i][0]=dataName;
                        eventDetails[i][1]="\n"+dataSDate+" "+"    "+" "+dataStartTime+"-"+dataEndTime;
                        eventDetails[i][2]="xyz";
                        

                    }
                    catch (JSONException e) {

                        throw new RuntimeException(e);
                    }



            }
            /*
            for (String s : eventDetails) {
                Log.v(LOG_TAG, "Data entry: " + s);
            }
            */
            //return null;
            return eventDetails;

        }


        //**********************************************
        @Override
        protected String[][] doInBackground(String... params) {


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

                    final String FORECAST_BASE_URL =
                            "https://graph.facebook.com/v2.8/794961210523337/events?";
                    final String APPID_PARAM = "access_token";

                    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendQueryParameter(APPID_PARAM, "EAACEdEose0cBAFYdjaBO3CZB6NVSJJXRDJbG0beZAcSN7lG7o7xmexZBmqitahEgLje00bwhUOmdTC89Dlsb1SVYhZCTwIoKcGQiDAnryV9p1p2Kza2uSlo6OIetE2cXi98mVOS9o3Mke13D5qGPP8JpRixhzaZC0VF59iBHcJgZDZD")
                            .build();

                    URL url = new URL(builtUri.toString());

                    Log.v(LOG_TAG, "Built URI " + builtUri.toString());


                    // Create the request to the API, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do
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
                    //Log.v(LOG_TAG, "Facebook JSON String:" + forecastJsonStr);

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
        protected void onPostExecute(String[][] result) {
            if (result != null) {
                //mForecastAdapter.clear();
                for (String[] dayForecastStr : result) {
                    mEventNameAdapter.add(dayForecastStr[0]+dayForecastStr[1]);
                    //mEventSummaryAdapter.add(dayForecastStr[1]);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }

    // ============================================== Google =======================================
    /**
     * Preconditions:
     * 1. Google Play Services installed,
     * 1. an account was selected
     * 3. has online access
     */
    private void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) { // has google service installed
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { // has an account
            chooseAccount();
        } else if (! isDeviceOnline()) { // has online access
            Log.d("#######", "No network connection available.");
        } else {
            // Preconditions satisfied
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                getActivity().getApplicationContext(), Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getActivity().getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.d("#######",
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getActivity().getApplicationContext());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            String dataName="No event Name";
            String dataDate;
            String dataStartTime;
            String dataEndTime;
            String timeStampG;
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<>();
            Events events = mService.events().list("primary")
                    .setMaxResults(eventLimit)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();


            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                DateTime end = event.getEnd().getDateTime();

                if ( start == null) {
                     start = event.getStart().getDate();
                }
                timeStampG=start.toString();
                dataDate=formatDateFromstring("yyyy-MM-dd","MMM dd, yyyy",timeStampG.substring(0,10));
                dataStartTime=formatTimeFromString("hh:mm:ss","hh:mm a",timeStampG.substring(11,19));
                dataName=event.getSummary();
                eventStrings.add(String.format("%s \n %s                       %s", dataName, dataDate, dataStartTime));
            }

            System.out.println("Result from get data from api"+eventStrings+"\n");
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                Log.d("#######", "No results returned.");
            } else {
                Log.d("#######", TextUtils.join("\n", output));
                //mForecastAdapter.clear();
                for (String s:output) mEventNameAdapter.add(s);
                mEventNameAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ApiCallFragment.REQUEST_AUTHORIZATION);
                } else {
                    Log.d("#######", "The following error occurred:\n"
                            + mLastError.getMessage());
                }
            } else {
                Log.d("#######", "Request cancelled.");
            }
        }
    }
}


