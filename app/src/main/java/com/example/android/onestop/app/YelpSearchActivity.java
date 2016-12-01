package com.example.android.onestop.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Zixiao on 11/23/2016.
 */

public class YelpSearchActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private double latitude;
    private double longitude;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    // UI Widgets.
    protected Button mStartUpdatesButton;
    protected Button mStopUpdatesButton;
    protected Button mYelpSearchButton;
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
    private ListView businessListView;

    private List<Business> mBusinessList;
    private SearchResultAdapter adapter;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;


    private YelpAPI yelpAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp_search);
        // Locate the UI widgets.
        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);
        mYelpSearchButton = (Button) findViewById(R.id.start_search_button);
        mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);

        // member variable init
        // TODO: get current location from Google Place API
        latitude = 33.7756;
        longitude = -84.3963;
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        mBusinessList = new ArrayList<>();


        final Bundle args = new Bundle();

        // Categories List stuff handling
        businessListView = (ListView) findViewById(R.id.business);


        // ==================== Google Location Update Services =====================
        buildGoogleApiClient();
        //startLocationUpdates();

        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdatesButtonHandler(v);
            }
        });

        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopUpdatesButtonHandler(v);
            }
        });

        Button searchButton = (Button) findViewById(R.id.start_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncYelpSearch();
            }
        });


    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i("GoogleServices", "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void yelpSearch() throws IOException {
        // setting up yelp api port
        /**
         * Construct a new {@code YelpAPIFactory}.
         *
         * @param consumerKey    the consumer key.
         * @param consumerSecret the consumer secret.
         * @param token          the access token.
         * @param tokenSecret    the token secret.
         * @see <a href="https://www.yelp.com/developers/manage_api_keys">https://www.yelp.com/developers/manage_api_keys</a>
         */
        YelpAPIFactory apiFactory = new YelpAPIFactory(
               this.getString(R.string.consumer_key),
               this.getString(R.string.consumer_secret),
               this.getString(R.string.token),
               this.getString(R.string.token_secret));
        yelpAPI = apiFactory.createAPI();

        // Example search: set params -------------------------------------
        Map<String, String> params = new HashMap<>();

        // general params
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        params.put("radius_filter", prefs.getString(SettingsActivity.PREF_KEY_SEARCH_RADIUS, Integer.toString(R.string.search_radius))); // max: 25 miles
        params.put("limit", prefs.getString(SettingsActivity.PREF_KEY_SEARCH_MAX, Integer.toString(R.string.search_result_max))); // number of business result
        params.put("sort", "1"); // 0: best matched; 1: distance; 2:highest rated*/
        // TODO: add 'category_filter' param according to users input: categories

        // -------------------- START search -------------------------------
        //
        // coordinates search example
        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(latitude)
                .longitude(longitude).build();
        Call<SearchResponse> call = yelpAPI.search(coordinate, params);

        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                // Update UI text with the searchResponse.
                int total = searchResponse.total();
                double lat = searchResponse.region().center().latitude();
                double lng = searchResponse.region().center().longitude();

                Log.d("YelpSearch", "Total Amount of search result:" + Integer.toString(total));
                Log.d("YelpSearch", "Lat: " + Double.toString(lat) + " Lng: " + Double.toString(lng));

                TextView mSearchCountTextView = (TextView) findViewById(R.id.search_count_text);
                mSearchCountTextView.setText("Total Count: " + Integer.toString(total));

            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                // HTTP error happened, do something to handle it.
                t.printStackTrace();
            }
        };
        //SearchResponse searchResponse = call.execute().body();
        call.enqueue(callback);

        /*
        int total = searchResponse.total();
        double lat = searchResponse.region().center().latitude();
        double lng = searchResponse.region().center().longitude();

        Log.d("YelpSearch", "Total Amount of search result:" + Integer.toString(total));
        Log.d("YelpSearch", "Lat: " + Double.toString(lat) + " Lng: " + Double.toString(lng));

        */
    }
    private void AsyncYelpSearch () {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                YelpAPIFactory apiFactory = new YelpAPIFactory(
                        getApplicationContext().getString(R.string.consumer_key),
                        getApplicationContext().getString(R.string.consumer_secret),
                        getApplicationContext().getString(R.string.token),
                        getApplicationContext().getString(R.string.token_secret));
                yelpAPI = apiFactory.createAPI();

                // Set searching params
                Map<String, String> ps = new HashMap<>();
                ps.put("radius_filter", "300"); // max: 40000 meters
                //ps.put("limit", "3"); // number of business result
                //ps.put("sort", "1"); // 0: best matched; 1: distance; 2:highest rated*/

                // coordinates search example
                CoordinateOptions coordinate = CoordinateOptions.builder()
                        .latitude(latitude)
                        .longitude(longitude).build();

                Call<SearchResponse> call = yelpAPI.search(coordinate, ps);
                try {
                    SearchResponse response = call.execute().body();
                    mBusinessList = response.businesses();
                    return Integer.toString(response.total());
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Exception " + e.toString();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                TextView mSearchCountTextView = (TextView) findViewById(R.id.search_count_text);
                adapter = new SearchResultAdapter(YelpSearchActivity.this, mBusinessList, latitude, longitude);
                businessListView.setAdapter(adapter);
                mSearchCountTextView.setText("Solution Found: " + result);
            }
        }.execute();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
            startLocationUpdates();
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
            stopLocationUpdates();
        }
    }

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */
    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i("GoogleServices", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        latitude = mCurrentLocation.getLatitude();
        longitude = mCurrentLocation.getLongitude();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        AsyncYelpSearch();
        updateUI();
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        mLatitudeTextView.setText(String.format("Latitude: %f", mCurrentLocation.getLatitude()));
        mLongitudeTextView.setText(String.format("Longitude: %f", mCurrentLocation.getLongitude()));
        mLastUpdateTimeTextView.setText(String.format("LastUpdateTime: %s", mLastUpdateTime));
    }
}
