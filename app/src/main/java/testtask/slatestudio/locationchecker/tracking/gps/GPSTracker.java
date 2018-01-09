package testtask.slatestudio.locationchecker.tracking.gps;

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
public class GPSTracker {

    private static final String TAG = GPSTracker.class.getSimpleName();
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final GPSTracker instance = new GPSTracker();
    private final LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private GPSCallback<GPSPoint> gpsCallback;

    @SuppressLint("MissingPermission")
    private GPSTracker() {
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
                if (gpsCallback != null) {
                    gpsCallback.onLocationChanged(gpsPoint);
                }
            }
        };

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(App.getInstance().getApplicationContext());
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    public static GPSTracker instance() {
        return instance;
    }

    public void onChange(GPSCallback<GPSPoint> GPSCallback) {
        this.gpsCallback = GPSCallback;
    }

    public void stop() {
        Log.i(TAG, "Stop tracking");
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @SuppressLint("MissingPermission")
    public void start() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

}

