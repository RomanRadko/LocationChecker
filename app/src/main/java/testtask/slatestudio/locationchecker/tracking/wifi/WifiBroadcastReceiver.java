package testtask.slatestudio.locationchecker.tracking.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * @author Roman
 * @since 1/9/2018.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = WifiBroadcastReceiver.class.getSimpleName();
    private static final String EMPTY = "";
    private Context context;
    private WifiReceiverCallback callback;

    public WifiBroadcastReceiver(WifiReceiverCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (SupplicantState.isValidState(state) && state == SupplicantState.COMPLETED) {
                callback.onUpdate(getWifiZoneName());
            }
        }
    }

    private String getWifiZoneName() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = wifiManager.getConnectionInfo();
        if (wifi != null) {
            String currentWifiName = wifi.getSSID().replace("\"", "");
            Log.d(TAG, "currentWifiName : " + currentWifiName);
            return currentWifiName;
        }
        return EMPTY;
    }
}
