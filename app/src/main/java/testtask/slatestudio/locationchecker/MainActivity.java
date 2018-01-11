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
import testtask.slatestudio.locationchecker.tracking.StatusListener;
import testtask.slatestudio.locationchecker.tracking.Status;
import testtask.slatestudio.locationchecker.tracking.LocationTracker;
import testtask.slatestudio.locationchecker.tracking.gps.GPSPoint;
import testtask.slatestudio.locationchecker.tracking.gps.GPSTracker;
import testtask.slatestudio.locationchecker.tracking.wifi.WifiBroadcastReceiver;

public class MainActivity extends AppCompatActivity implements StatusListener {

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

    private WifiBroadcastReceiver wifiReceiver;
    private LocationTracker locationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        locationTracker = new LocationTracker(this);
        fetchNetworkName();
        markIsLocated(locationTracker.isInRange());
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
        String latitudeTxt = geoPointLatitudeInput.getText().toString();
        String longtitudeTxt = geoPointLongtitudeInput.getText().toString();
        String radiusTxt = radiusInput.getText().toString();
        if (!latitudeTxt.isEmpty() && !longtitudeTxt.isEmpty()) {
            double geoPointLatitude = latitudeTxt.equals(".") ? 0 : Double.parseDouble(geoPointLatitudeInput.getText().toString());
            double geoPointLongtitude = longtitudeTxt.equals(".") ? 0 : Double.parseDouble(geoPointLongtitudeInput.getText().toString());
            locationTracker.setTargetPoint(new GPSPoint(geoPointLatitude, geoPointLongtitude));
        }
        if (!radiusTxt.isEmpty()) {
            int radius = Integer.parseInt(radiusInput.getText().toString());
            locationTracker.setTargetRadius(radius);
        }
        mapView.setPoints(locationTracker.getStatus().getCurrentLocation(),
                locationTracker.getStatus().getTargetLocation(),
                locationTracker.getStatus().getRadius());

    }

    @OnTextChanged(R.id.network_name)
    void refreshName() {
        locationTracker.setTargetNetworkName(networkNameInput.getText().toString());
        markIsInWifiRange(locationTracker.isInWifiRange());
    }

    @Override
    public void onStatusChanged(Status status) {
        markIsLocated(status.isInRange());
        markIsInWifiRange(status.isInWifiRange());
        markIsInGeoRange(status.isInGeoRange());
        gpsPointStatus.setText(status.getCurrentLocation().toString());
    }

    private void fetchNetworkName() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wifiManager.getConnectionInfo();
        if (wifi != null) {
            String currentWifiName = wifi.getSSID().replace("\"", "");
            Log.d(TAG, "currentWifiName : " + currentWifiName);
            locationTracker.setConnectedNetworkName(currentWifiName);
        }
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
        GPSTracker.instance().onChange(locationTracker);
    }

    private boolean hasPermissions() {
        final int resultFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        final int resultCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return resultFineLocation == PackageManager.PERMISSION_GRANTED && resultCoarseLocation == PackageManager.PERMISSION_GRANTED;
    }

    private void initWifiReceiver() {
        wifiReceiver = new WifiBroadcastReceiver(locationTracker);
        IntentFilter updateIntentFilter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, updateIntentFilter);
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
}
