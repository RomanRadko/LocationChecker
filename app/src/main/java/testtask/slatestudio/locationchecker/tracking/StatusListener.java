package testtask.slatestudio.locationchecker.tracking;

import testtask.slatestudio.locationchecker.tracking.gps.GPSPoint;

/**
 * @author Roman
 * @since 1/11/2018.
 */
public interface StatusListener {

    void onStatusChanged(Status status);

}
