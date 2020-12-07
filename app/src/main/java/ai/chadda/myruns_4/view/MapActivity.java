package ai.chadda.myruns_4.view;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import ai.chadda.myruns_4.R;
import ai.chadda.myruns_4.controller.ActivityRecognitionService;
import ai.chadda.myruns_4.controller.BackgroundTask;
import ai.chadda.myruns_4.controller.TrackingService;
import ai.chadda.myruns_4.view.fragments.HistoryFragment;
import ai.chadda.myruns_4.view.fragments.MyRunsDialogFragment;
import ai.chadda.myruns_4.view.fragments.StartFragment;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Constants
    private static final String ADD_INFO_FLAG = "add_info";
    private static final String DELETE_ROW_FLAG = "delete_row";
    public static final String SOURCE_FLAG_GPS = "gps";
    public static final String SOURCE_FLAG_AUTO = "automatic";
    private static final String INPUT_TYPE_GPS = "2";
    private static final String INPUT_TYPE_AUTO = "3";
    private static final double MS_TO_MPH = 2.237;
    private static final double M_TO_FT = 3.281;
    private static final double KM_TO_MI = 1.60934;
    private static final double CAL_COEF = 0.06;
    private static final double MS_COEF = 1000;
    private static final String LOCATION = "location";
    private static final String START = "Start";
    private static final String YOU_ARE_HERE = "You are here";
    private String mAutomaticType = "Searching...";

    // Global widgets
    private Marker mCurrentMarker;
    private Marker mStartMarker;
    private GoogleMap mMap;
    private Polyline mPolyline;
    private TextView mActivityText;
    private TextView mSpeedText;
    private TextView mAvgSpeedText;
    private TextView mClimbedText;
    private TextView mCalorieText;
    private TextView mDistanceText;
    public GoogleApiClient mApiClient;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private PendingIntent mPendingIntent;

    // Global data
    private double mSpeed = 0;
    private double mAvgSpeed = 0;
    private double mClimbed = 0;
    private double mCalorie = 0;
    private double mDistance = 0;
    private double mTimeElapsed = 0;
    private double mStartAltitude;
    private long mStartTime;
    public static boolean isActive;
    private List<LatLng> mPoints;
    public static ArrayList mMapList = new ArrayList();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up layout
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_map);

        mActivityText = findViewById(R.id.map_activity_text);
        mSpeedText = findViewById(R.id.map_speed_text);
        mAvgSpeedText = findViewById(R.id.map_avgspeed_text);
        mClimbedText = findViewById(R.id.map_climbed_text);
        mCalorieText = findViewById(R.id.map_calorie_text);
        mDistanceText = findViewById(R.id.map_distance_text);

        // Set up fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        // Start tracking location
        isActive = true;

        // Determine behavior based on intent
        if (getIntent().getExtras() != null) {

            // From StartFragment
            if (getIntent().getExtras().getString(SOURCE_FLAG_GPS).equals(StartFragment.SOURCE_FLAG_START)) {

                // Load data based on unit preference
                if (MyRunsDialogFragment.METRIC_UNITS_FLAG) {
                    mActivityText.setText("Activity: " + getIntent().getExtras().getString(StartFragment.ACTIVITY_TYPE_KEY));
                    mSpeedText.setText(String.format("Speed: %.2f m/s", mSpeed));
                    mAvgSpeedText.setText(String.format("Average Speed: %.2f m/s", mAvgSpeed));
                    mClimbedText.setText(String.format("Climbed: %.2f m", mClimbed));
                    mCalorieText.setText(String.format("Calorie: %.2f cal", mCalorie));
                    mDistanceText.setText(String.format("Distance: %.2f km", mDistance));
                } else {
                    mActivityText.setText("Activity: " + getIntent().getExtras().getString(StartFragment.ACTIVITY_TYPE_KEY));
                    mSpeedText.setText(String.format("Speed: %.2f mph", mSpeed));
                    mAvgSpeedText.setText(String.format("Average Speed: %.2f mph", mAvgSpeed));
                    mClimbedText.setText(String.format("Climbed: %.2f ft", mClimbed));
                    mCalorieText.setText(String.format("Calorie: %.2f cal", mCalorie));
                    mDistanceText.setText(String.format("Distance: %.2f mi", mDistance));
                }

                // Request permissions for location tracking
                if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
                } else {
                    startForegroundService(new Intent(this, TrackingService.class));
                }

                // From StartFragment automatic option
                if (getIntent().getExtras().getString(SOURCE_FLAG_AUTO).equals(StartFragment.SOURCE_FLAG_START)) {
                    mApiClient = new GoogleApiClient.Builder(MapActivity.this).addApi(ActivityRecognition.API).addConnectionCallbacks(MapActivity.this).addOnConnectionFailedListener(MapActivity.this).build();
                    mApiClient.connect();
                    mActivityText.setText("Activity: " + mAutomaticType);
                }

                // Start broadcast receiver on location tracking
                LocalBroadcastManager.getInstance(this).registerReceiver(mLocationBroadcastReceiver, new IntentFilter(TrackingService.BROADCAST_LOCATION));

            // From history fragment
            } else if (getIntent().getExtras().getString(SOURCE_FLAG_GPS).equals(HistoryFragment.SOURCE_FLAG_HISTORY)) {

                // Set up data based on unit preference
                if (MyRunsDialogFragment.METRIC_UNITS_FLAG) {
                    mActivityText.setText("Activity: " + mMapList.get(0));
                    mSpeedText.setText("Speed: " + mMapList.get(1) + " m/s");
                    mAvgSpeedText.setText("Average Speed: " + mMapList.get(2) + " m/s");
                    mClimbedText.setText("Climbed: " + mMapList.get(3) + " m");
                    mCalorieText.setText("Calorie: " + mMapList.get(4) + " cal");
                    mDistanceText.setText("Distance: " + mMapList.get(5) + " km");
                } else {
                    // Convert units
                    double mSpeedDisplay = Double.parseDouble((String) mMapList.get(1)) * MS_TO_MPH;
                    double mAvgSpeedDisplay = Double.parseDouble((String) mMapList.get(2)) * MS_TO_MPH;
                    double mClimbedDisplay = Double.parseDouble((String) mMapList.get(3)) *  M_TO_FT;
                    double mDistanceDisplay = Double.parseDouble((String) mMapList.get(5)) / KM_TO_MI;

                    mActivityText.setText("Activity: " + mMapList.get(0));
                    mSpeedText.setText(String.format("Speed: %.2f mph", mSpeedDisplay));
                    mAvgSpeedText.setText(String.format("Average Speed: %.2f mph", mAvgSpeedDisplay));
                    mClimbedText.setText(String.format("Climbed: %.2f ft", mClimbedDisplay));
                    mCalorieText.setText("Calorie: " + mMapList.get(4) + " cal");
                    mDistanceText.setText(String.format("Distance: %.2f mi", mDistanceDisplay));
                }
            }
        }
    }

    // Set up broadcast receiver for location tracking
    BroadcastReceiver mLocationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TrackingService.BROADCAST_LOCATION)) {
                Location location = intent.getParcelableExtra(LOCATION);
                LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());

                // Initialize start metrics at start
                if (mStartMarker == null) {
                    mStartMarker = mMap.addMarker(new MarkerOptions().position(coordinates).title(START).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mStartAltitude = location.getAltitude();
                    mStartTime = System.currentTimeMillis();
                }

                // Update current location marker
                if (mCurrentMarker != null) mCurrentMarker.remove();
                mCurrentMarker = mMap.addMarker(new MarkerOptions().position(coordinates).title(YOU_ARE_HERE));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17));

                // Create/extend polyline
                if (mPolyline == null) {
                    mPolyline = mMap.addPolyline(new PolylineOptions().add(coordinates).color(Color.CYAN));
                } else {
                    mPoints =  mPolyline.getPoints();
                    if (!mPoints.contains(coordinates)) {

                        // Update metrics
                        mDistance += getDistance(mPoints.get(0), coordinates);
                        mCalorie = mDistance * CAL_COEF;
                        mClimbed = location.getAltitude() - mStartAltitude;
                        mSpeed = location.getSpeed();
                        mTimeElapsed = (System.currentTimeMillis() - mStartTime) / MS_COEF;
                        mAvgSpeed = MS_COEF * mDistance / mTimeElapsed;

                        // Add to polyline
                        mPoints.add(coordinates);
                        mPolyline.setPoints(mPoints);

                        // Update metric screen
                        if (MyRunsDialogFragment.METRIC_UNITS_FLAG) {
                            mSpeedText.setText(String.format("Speed: %.2f m/s", mSpeed));
                            mAvgSpeedText.setText(String.format("Average Speed: %.2f m/s", mAvgSpeed));
                            mClimbedText.setText(String.format("Climbed: %.2f m", mClimbed));
                            mCalorieText.setText(String.format("Calorie: %.2f cal", mCalorie));
                            mDistanceText.setText(String.format("Distance: %.2f km", mDistance));
                        } else {
                            double mSpeedDisplay = mSpeed * MS_TO_MPH;
                            double mAvgSpeedDisplay = mAvgSpeed * MS_TO_MPH;
                            double mClimbedDisplay = mClimbed * M_TO_FT;
                            double mDistanceDisplay = mDistance / KM_TO_MI;

                            mSpeedText.setText(String.format("Speed: %.2f mph", mSpeedDisplay));
                            mAvgSpeedText.setText(String.format("Average Speed: %.2f mph", mAvgSpeedDisplay));
                            mClimbedText.setText(String.format("Climbed: %.2f ft", mClimbedDisplay));
                            mCalorieText.setText(String.format("Calorie: %.2f cal", mCalorie));
                            mDistanceText.setText(String.format("Distance: %.2f mi", mDistanceDisplay));
                        }
                    }

                    // Get activity from activity recognition
                    if (getIntent().getExtras().getString(SOURCE_FLAG_AUTO).equals(StartFragment.SOURCE_FLAG_START)) {
                        if (ActivityRecognitionService.WALK_FLAG) {
                            mActivityText.setText("Activity: Walking");
                        } else if (ActivityRecognitionService.RUN_FLAG) {
                            mActivityText.setText("Activity: Running");
                        } else if (ActivityRecognitionService.STILL_FLAG) {
                            mActivityText.setText("Activity: Standing");
                        } else if (ActivityRecognitionService.BIKE_FLAG) {
                            mActivityText.setText("Activity: Cycling");
                        } else if (ActivityRecognitionService.IDK_FLAG) {
                            mActivityText.setText("Activity: Unknown");
                        } else {
                            mActivityText.setText("Activity: Searching...");
                        }
                    }

                }
            }
        }
    };

    // Set up options bar based on intent
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString(SOURCE_FLAG_GPS).equals(StartFragment.SOURCE_FLAG_START)) {
                inflater.inflate(R.menu.save_bar, menu);
            } else if (getIntent().getExtras().getString(SOURCE_FLAG_GPS).equals(HistoryFragment.SOURCE_FLAG_HISTORY)) {
                inflater.inflate(R.menu.delete_bar, menu);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_button:
                // Stop location tracking
                isActive = false;

                // Stop broadcast receiver
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationBroadcastReceiver);

                // Get date and time
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm");
                String formattedDateTime = df.format(c);

                // Calculate metrics
                mSpeed = mDistance/mTimeElapsed;
                int mTimeInMin = (int) mTimeElapsed/60;
                DecimalFormat formatDecimal = new DecimalFormat("#.##");
                mSpeed = Double.valueOf(formatDecimal.format(mSpeed));
                mDistance = Double.valueOf(formatDecimal.format(mDistance));
                mAvgSpeed = Double.valueOf(formatDecimal.format(mAvgSpeed));
                int calories = (int) mCalorie;
                mClimbed = Double.valueOf(formatDecimal.format(mClimbed));

                // Convert LatLngs to JSON
                String GPSJSON = new Gson().toJson(mPoints);

                // Set up and start BackgroundTask
                // GPS option
                if (!getIntent().getExtras().getString(SOURCE_FLAG_AUTO).equals(StartFragment.SOURCE_FLAG_START)) {
                    BackgroundTask backgroundTask = new BackgroundTask(this);
                    backgroundTask.execute(ADD_INFO_FLAG, INPUT_TYPE_GPS, getIntent().getExtras().getString(StartFragment.ACTIVITY_TYPE_KEY), formattedDateTime,
                            String.valueOf(mTimeInMin), String.valueOf(mDistance), String.valueOf(mSpeed), String.valueOf(mAvgSpeed), String.valueOf(calories), String.valueOf(mClimbed), "0", "", "0", GPSJSON);
                    finish();

                // Automatic option
                } else {
                    int max = new TreeSet<>(Arrays.asList(ActivityRecognitionService.walkCount, ActivityRecognitionService.standingCount, ActivityRecognitionService.runningCount, ActivityRecognitionService.cyclingCount, ActivityRecognitionService.unknownCount)).last();
                    if (max == ActivityRecognitionService.walkCount) {
                        mAutomaticType = "Walking";
                    } else if (max == ActivityRecognitionService.standingCount) {
                        mAutomaticType = "Standing";
                    } else if (max == ActivityRecognitionService.runningCount) {
                        mAutomaticType = "Running";
                    } else if (max == ActivityRecognitionService.cyclingCount) {
                        mAutomaticType = "Cycling";
                    } else {
                        mAutomaticType = "Unknown";
                    }
                    BackgroundTask backgroundTask = new BackgroundTask(this);
                    backgroundTask.execute(ADD_INFO_FLAG, INPUT_TYPE_AUTO, mAutomaticType, formattedDateTime,
                            String.valueOf(mTimeInMin), String.valueOf(mDistance), String.valueOf(mSpeed), String.valueOf(mAvgSpeed), String.valueOf(calories), String.valueOf(mClimbed), "0", "", "0", GPSJSON);
                    finish();
                }
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.delete_button:
                // Delete activity from db
                BackgroundTask backgroundTaskDelete = new BackgroundTask(this);
                backgroundTaskDelete.execute(DELETE_ROW_FLAG, mMapList.get(7).toString());

                // Delete activity from local db
                mMapList.clear();
                finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isActive = false;
        mMapList.clear();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (getIntent().getExtras() != null) {
            // Display route from history
            if (getIntent().getExtras().getString(SOURCE_FLAG_GPS).equals(HistoryFragment.SOURCE_FLAG_HISTORY)) {
                Gson gson = new GsonBuilder().create();
                try {
                    JSONArray obj = new JSONArray(mMapList.get(6).toString());
                    Type listType = new TypeToken<ArrayList<LatLng>>() {
                    }.getType();
                    ArrayList<LatLng> coordinates = gson.fromJson(String.valueOf(obj), listType);
                    mStartMarker = mMap.addMarker(new MarkerOptions().position(coordinates.get(0)).title(START).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    mCurrentMarker = mMap.addMarker(new MarkerOptions().position(coordinates.get(coordinates.size() - 1)).title(YOU_ARE_HERE));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates.get(coordinates.size() - 1), 17));
                    mPolyline = mMap.addPolyline(new PolylineOptions().addAll(coordinates).color(Color.CYAN));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            startForegroundService(new Intent(this, TrackingService.class));
        else
            finish();
    }

    // Calculates distance in km between two LatLng's
    public double getDistance(LatLng p1, LatLng p2) {
        double latA = Math.toRadians(p1.latitude);
        double lonA = Math.toRadians(p1.longitude);
        double latB = Math.toRadians(p2.latitude);
        double lonB = Math.toRadians(p2.longitude);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB-lonA)) + (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang * 6371;
        return dist;
    }

    @Override
    // Set up activity recognition api
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(MapActivity.this, ActivityRecognitionService.class);
        mPendingIntent = PendingIntent.getService(MapActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mActivityRecognitionClient = ActivityRecognition.getClient(this);
        Task task = mActivityRecognitionClient.requestActivityUpdates( 1000, mPendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // Turns off activity recognition
    public void removeActivityUpdatesHandler() {
        if(mActivityRecognitionClient != null){
            Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(mPendingIntent);
        }
    }

    @Override
    // Shut everything off
    protected void onDestroy() {
        super.onDestroy();
        isActive = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationBroadcastReceiver);
        removeActivityUpdatesHandler();
        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        getApplicationContext().sendBroadcast(closeIntent);
        mMapList.clear();
    }


}
