package ai.chadda.myruns_4.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import ai.chadda.myruns_4.R;
import ai.chadda.myruns_4.view.MapActivity;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class TrackingService extends Service {

    // Constants
    public static final String BROADCAST_LOCATION = "location update";
    private static final String GET_LOCATION = "location";
    private static final String CHANNEL = "MyRuns";
    private static final String CHANNEL_ID = "TrackingService";
    private static final String NOTIFICATION_INFO = "Tracking your location";
    private static final int UPDATE_INTERVAL = 5000;
    public static final int FAST_INTERVAL = 1000;
    public static final int NOTIFICATION_ID = 1;

    // Global
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // Set up location request
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FAST_INTERVAL);

        // Set up location updates
        getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());

    }

    // Receives and broadcasts location
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Intent intent = new Intent(BROADCAST_LOCATION);
            intent.putExtra(GET_LOCATION, locationResult.getLastLocation());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            if (!MapActivity.isActive) {
                onDestroy();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        // Set up intents
        Intent nIntent = new Intent(this, MapActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, nIntent, 0);

        // Set up notification channel
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL, NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(this, CHANNEL_ID).setContentTitle(CHANNEL).setContentText(NOTIFICATION_INFO).setSmallIcon(R.drawable.icon).setOngoing(false).setContentIntent(pIntent).build();

        // Post notification
        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        notificationManager.cancelAll();
    }
}
