package testtask.slatestudio.locationchecker.geotracking;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import testtask.slatestudio.locationchecker.App;

/**
 * @author Roman
 * @since 1/9/2018.
 */
public class GeoTracker {

    private static final String TAG = GeoTracker.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final GeoTracker instance = new GeoTracker();
    private final LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GPSCallback<GPSPoint> GPSCallback;

    @SuppressLint("MissingPermission")
    private GeoTracker() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location currentLocation = locationResult.getLastLocation();
                GPSPoint gpsPoint = new GPSPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
                Log.i(TAG, "Geo callback results: " + gpsPoint);
                if (null != GPSCallback) {
                    GPSCallback.update(gpsPoint);
                }
            }
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(App.getInstance().getApplicationContext());
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    public static GeoTracker instance() {
        return instance;
    }

    public void onChange(GPSCallback<GPSPoint> GPSCallback) {
        this.GPSCallback = GPSCallback;
    }

    public void stop() {
        Log.i(TAG, "Stop tracking");
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    public void refresh() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

}

