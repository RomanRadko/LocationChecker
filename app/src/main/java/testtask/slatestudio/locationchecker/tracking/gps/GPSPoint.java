package testtask.slatestudio.locationchecker.tracking.gps;

/**
 * @author Roman
 * @since 1/9/2018.
 */
public class GPSPoint {

    private double lat;
    private double lon;

    GPSPoint(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    @Override
    public String toString() {
        return "(" + lat + ", " + lon + ")";
    }

    public boolean isInRange(Double inLatitude, Double inLongtitude, int radius) {
        //TODO: add check here
        return false;
    }
}

