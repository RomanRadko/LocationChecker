package testtask.slatestudio.locationchecker.tracking;

import android.util.Log;

import testtask.slatestudio.locationchecker.tracking.gps.GPSCallback;
import testtask.slatestudio.locationchecker.tracking.gps.GPSPoint;
import testtask.slatestudio.locationchecker.tracking.wifi.WifiReceiverCallback;

/**
 * @author Roman
 * @since 1/11/2018.
 */
public class LocationTracker implements WifiReceiverCallback, GPSCallback {

    private static final String TAG = LocationTracker.class.getSimpleName();
    private StatusListener listener;
    private GPSPoint currentLocation;
    private GPSPoint targetLocation;
    private String targetNetworkName;
    private int radius;
    private String currentWifiName;
    private Status status;

    public LocationTracker() {
    }

    public LocationTracker(StatusListener listener) {
        this.listener = listener;
    }

    @Override
    public void onLocationChanged(GPSPoint newLocation) {
        Log.d(TAG, "newLocation: " + newLocation);
        currentLocation = newLocation;
        changeStatus();
    }

    @Override
    public void onCurrentWifiChanged(String wifiNetworkName) {
        changeStatus();
    }

    public void setTargetNetworkName(String networkName) {
        this.targetNetworkName = networkName;
    }

    public void setTargetRadius(int targetRadius) {
        radius = targetRadius;
    }

    public boolean isInRange() {
        return isInWifiRange() || isInGeoRange();
    }

    public void setConnectedNetworkName(String connectedNetworkName) {
        this.currentWifiName = connectedNetworkName;
    }

    public void setTargetPoint(GPSPoint targetPoint) {
        targetLocation = targetPoint;
    }

    public void setCurrentPoint(GPSPoint currentPoint) {
        currentLocation = currentPoint;
    }

    public boolean isInWifiRange() {
        return currentWifiName != null && targetNetworkName != null && currentWifiName.equals(targetNetworkName);
    }

    public Status getStatus() {
        return status;
    }

    private boolean isInGeoRange() {
        return targetLocation != null && currentLocation.isInRange(targetLocation.getLat(), targetLocation.getLon(), radius);
    }

    private void changeStatus() {
        status = Status.newBuilder()
                .setInRange(isInRange())
                .setInWifiRange(isInWifiRange())
                .setInGeoRange(isInGeoRange())
                .setCurrentLocation(currentLocation)
                .setTargetLocation(targetLocation)
                .setRadius(radius).build();
        listener.onStatusChanged(status);
    }
}
