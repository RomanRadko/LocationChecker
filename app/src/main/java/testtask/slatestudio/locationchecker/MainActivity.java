package testtask.slatestudio.locationchecker;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import testtask.slatestudio.locationchecker.map.MapView;
import testtask.slatestudio.locationchecker.tracking.gps.GPSCallback;
import testtask.slatestudio.locationchecker.tracking.gps.GPSPoint;
import testtask.slatestudio.locationchecker.tracking.gps.GPSTracker;
import testtask.slatestudio.locationchecker.tracking.wifi.WifiBroadcastReceiver;
import testtask.slatestudio.locationchecker.tracking.wifi.WifiReceiverCallback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int READ_LOCATION_PERMISSION_REQUEST_CODE = 1;

    @BindView(R.id.is_in_range)
    TextView isInRangeValue;

    @BindView(R.id.gps_point_status)
    TextView gpsPointStatus;

    @BindView(R.id.gps_in_range_status)
    TextView gpsRangeStatus;

    @BindView(R.id.wifi_zone_status)
    TextView wifiRangeStatus;

    @BindView(R.id.geo_point_latitude)
    EditText geoPointLatitudeInput;

    @BindView(R.id.geo_point_longtitude)
    EditText geoPointLongtitudeInput;

    @BindView(R.id.radius)
    EditText radiusInput;

    @BindView(R.id.network_name)
    EditText networkNameInput;

    @BindView(R.id.map_view)
    MapView mapView;

    private double geoPointLatitude;
    private double geoPointLongtitude;
    private int radius;
    private String networkName;
    private WifiBroadcastReceiver wifiReceiver;
    private GPSPoint destinatedLocation;
    private GPSPoint currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_LOCATION_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            boolean granted = true;
            for (int i = 0; i < permissions.length; i++) {
                granted = granted && (grantResults[i] == PackageManager.PERMISSION_GRANTED);
            }
            onPermissionResult(granted);
        } else {
            onPermissionResult(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initWifiReceiver();
        checkPermissionAndStartGeoTracker();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GPSTracker.instance().stop();
    }

    @OnTextChanged({R.id.geo_point_latitude, R.id.geo_point_longtitude, R.id.radius})
    void recalculate() {
        fetchParameters();
        markIsLocated(isInGeoRange());
        markIsInGeoRange(isInGeoRange());
    }

    @OnTextChanged(R.id.network_name)
    void refreshName() {
        networkName = networkNameInput.getText().toString();
        markIsLocated(isInWifiZone());
        markIsInWifiRange(isInWifiZone());
    }

    private void checkPermissionAndStartGeoTracker() {
        if (hasPermissions()) {
            onPermissionResult(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    READ_LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void onPermissionResult(boolean permissionGranted) {
        if (permissionGranted) {
            startGeoTracker();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    READ_LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private void startGeoTracker() {
        GPSTracker.instance().start();
        GPSTracker.instance().onChange(new GPSCallback<GPSPoint>() {
            @Override
            public void onLocationChanged(GPSPoint newLocation) {
                Log.d(TAG, "newLocation: " + newLocation);
                mapView.setPoints(newLocation, destinatedLocation, radius);
                currentLocation = newLocation;
                gpsPointStatus.setText(newLocation.toString());
                markIsLocated(isInWifiZone() || isInGeoRange());
            }
        });
    }

    private boolean hasPermissions() {
        final int resultFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        final int resultCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return resultFineLocation == PackageManager.PERMISSION_GRANTED && resultCoarseLocation == PackageManager.PERMISSION_GRANTED;
    }

    private void initWifiReceiver() {
        wifiReceiver = new WifiBroadcastReceiver(new WifiReceiverCallback() {
            @Override
            public void onCurrentWifiChanged(String wifiName) {
                markIsLocated(networkName.equals(wifiName));
            }
        });
        IntentFilter updateIntentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, updateIntentFilter);
    }

    private void fetchParameters() {
        String latitudeTxt = geoPointLatitudeInput.getText().toString();
        String longtitudeTxt = geoPointLongtitudeInput.getText().toString();
        String radiusTxt = radiusInput.getText().toString();
        if (!latitudeTxt.isEmpty()) {
            geoPointLatitude = latitudeTxt.equals(".") ? 0 : Double.parseDouble(geoPointLatitudeInput.getText().toString());
        }
        if (!longtitudeTxt.isEmpty()) {
            geoPointLongtitude = longtitudeTxt.equals(".") ? 0 : Double.parseDouble(geoPointLongtitudeInput.getText().toString());
        }
        if (!radiusTxt.isEmpty()) {
            radius = Integer.parseInt(radiusInput.getText().toString());
        }
        destinatedLocation = new GPSPoint(geoPointLatitude, geoPointLongtitude);
    }

    private void markIsLocated(boolean isLocated) {
        isInRangeValue.setText(isLocated ? R.string.in_range : R.string.not_in_ranage);
    }

    private void markIsInGeoRange(boolean isLocated) {
        gpsRangeStatus.setText(isLocated ? R.string.in_range : R.string.not_in_ranage);
    }

    private void markIsInWifiRange(boolean isLocated) {
        wifiRangeStatus.setText(isLocated ? R.string.in_range : R.string.not_in_ranage);
    }

    private boolean isInWifiZone() {
        boolean connected = false;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wifiManager.getConnectionInfo();
        if (wifi != null && networkName != null && !networkName.isEmpty()) {
            String currentWifiName = wifi.getSSID().replace("\"", "");
            Log.d(TAG, "currentWifiName : " + currentWifiName);
            connected = networkName.equals(currentWifiName);
            Log.d(TAG, "connected : " + connected);
        }
        return connected;
    }

    private boolean isInGeoRange() {
        return currentLocation.isInRange(geoPointLatitude, geoPointLongtitude, radius);
    }
}
