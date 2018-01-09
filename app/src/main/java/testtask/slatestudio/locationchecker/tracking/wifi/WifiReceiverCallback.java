package testtask.slatestudio.locationchecker.tracking.wifi;

/**
 * @author Roman Radko
 * @since 1/9/2018.
 */
public interface WifiReceiverCallback {

    void onCurrentWifiChanged(String wifiNetworkName);

}
