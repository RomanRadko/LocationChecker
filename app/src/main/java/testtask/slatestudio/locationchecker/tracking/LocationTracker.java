package testtask.slatestudio.locationchecker.tracking;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import testtask.slatestudio.locationchecker.App;
import testtask.slatestudio.locationchecker.tracking.gps.GPSCallback;
import testtask.slatestudio.locationchecker.tracking.gps.GPSPoint;
import testtask.slatestudio.locationchecker.tracking.wifi.WifiReceiverCallback;

/**
 * @author Roman
 * @since 1/11/2018.
 */
public class LocationTracker implements WifiReceiverCallback, GPSCallback {

    private static final String TAG = LocationTracker.class.getSimpleName();
    private final StatusListener listener;
    private GPSPoint currentLocation;
    private GPSPoint targetLocation;
    private String networkName;
    private int radius;
    private String currentWifiName;
    private Status status;

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
        this.networkName = networkName;
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
        this.targetLocation = targetPoint;
    }

    public boolean isInWifiRange() {
        boolean connected = false;
        WifiManager wifiManager = (WifiManager) App.getInstance().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wifiManager.getConnectionInfo();
        if (wifi != null && networkName != null && !networkName.isEmpty()) {
            String currentWifiName = wifi.getSSID().replace("\"", "");
            Log.d(TAG, "currentWifiName : " + currentWifiName);
            connected = networkName.equals(currentWifiName);
            Log.d(TAG, "connected : " + connected);
        }
        return connected;
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
                .setRadius(radius)
                .setCurrentWifiName(currentWifiName).build();
        listener.onStatusChanged(status);
    }
}
