package testtask.slatestudio.locationchecker;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.is_in_range)
    TextView isInRangeLabel;

    @BindView(R.id.geo_point_latitude)
    EditText geoPointLatitudeInput;

    @BindView(R.id.geo_point_longtitude)
    EditText geoPointLongtitudeInput;

    @BindView(R.id.radius)
    EditText radiusInput;

    @BindView(R.id.network_name)
    EditText networkNameInput;

    @OnClick(R.id.check_btn)
    void check() {
        isInRangeLabel.setText(isInLocation() ? R.string.in_range : R.string.not_in_ranage);
    }

    private int geoPointLatitiude;
    private int geoPointLongtitude;
    private int radius;
    private String networkName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fetchParameters();
        isInLocation();
    }

    private void fetchParameters() {
        geoPointLatitiude = Integer.parseInt(geoPointLatitudeInput.getText().toString());
        geoPointLongtitude = Integer.parseInt(geoPointLongtitudeInput.getText().toString());
        radius = Integer.parseInt(radiusInput.getText().toString());
        networkName = networkNameInput.getText().toString();
    }

    private boolean isInWifiZone() {
        boolean connected = false;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wifiManager.getConnectionInfo();
        if (wifi != null) {
            String currentWifiName = wifi.getSSID().replace("\"", "");
            Log.d(TAG, "currentWifiName : " + currentWifiName);
            connected = networkName.equals(currentWifiName);
            Log.d(TAG, "connected : " + connected);
        }
        return connected;
    }

    private boolean isInGeoZone() {
        //TODO:
        return false;
    }

    private boolean isInLocation() {
        return isInWifiZone();
    }

}
