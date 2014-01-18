package de.fhb.mi.paperfly.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**
 * A background service which gets the actual location of the user.
 *
 * @author Christoph Ott
 * @see android.app.Service
 */
public class BackgroundLocationService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = BackgroundLocationService.class.getSimpleName();
    private static final int LOCATION_LOOKUP_INTERVAL = 60 + 1000;
    private static final int LOCATION_LOOKUP_INTERVAL_FASTEST = 30 + 1000;
    IBinder mBinder = new LocationBinder();
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private boolean mInProgress;
    private Boolean servicesAvailable = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();

        mInProgress = false;
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the update interval
        mLocationRequest.setInterval(LOCATION_LOOKUP_INTERVAL);
        // Set the fastest update interval
        mLocationRequest.setFastestInterval(LOCATION_LOOKUP_INTERVAL_FASTEST);

        servicesAvailable = servicesAvailable(this);

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
    }

    @Override
    public void onDestroy() {
        // Turn off the request flag
        mInProgress = false;
        if (servicesAvailable && mLocationClient != null) {
            mLocationClient.removeLocationUpdates(this);
            // Destroy the current location client
            mLocationClient = null;
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStartCommand");
        super.onStartCommand(intent, flags, startId);

        if (!servicesAvailable || mLocationClient.isConnected() || mInProgress) {
            return START_STICKY;
        }

        setUpLocationClientIfNeeded();
        if (!mLocationClient.isConnected() || !mLocationClient.isConnecting() && !mInProgress) {
            mInProgress = true;
            mLocationClient.connect();
        }

        return START_STICKY;
    }

    /**
     * Create a new location client, using the enclosing class to
     * handle callbacks.
     */
    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null)
            mLocationClient = new LocationClient(this, this, this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "connected");
        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected");
        // Turn off the request flag
        mInProgress = false;
        // Destroy the current location client
        mLocationClient = null;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        mInProgress = false;

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {

            // If no resolution is available, display an error dialog
        } else {

        }
    }

    /**
     * Checks if the GooglePlayServices are available.
     *
     * @param context the application context
     *
     * @return true if the service is available, false if not
     */
    public static boolean servicesAvailable(Context context) {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(TAG, "PlayServices available");
            return true;
        } else {
            Log.d(TAG, "PlayServices not available");
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
    }

    /**
     * Gets the last known location of the user.
     *
     * @return the last known location of the user
     */
    public Location getCurrentLocation() {
        return mLocationClient.getLastLocation();
    }

    /**
     * A Binder for the BackgroundLocationService
     *
     * @see android.os.Binder
     */
    public class LocationBinder extends Binder {
        public BackgroundLocationService getServiceInstance() {
            return BackgroundLocationService.this;
        }
    }
}
