package testtask.slatestudio.locationchecker.tracking.gps;

/**
 * @author Roman
 * @since 1/9/2018.
 */
public class GPSPoint {

    private static final int EARTH_RADIUS_IN_METERS = 6371000;
    private double lat;
    private double lon;

    public GPSPoint() {
    }

    GPSPoint(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    @Override
    public String toString() {
        return "(" + lat + ", " + lon + ")";
    }

    public boolean isInRange(Double inLatitude, Double inLongtitude, int radius) {
        if (radius <= 0 || inLatitude < -90 || inLatitude > 90 || inLongtitude < -180 || inLongtitude > 180) {
            return false;
        }
        double dist = Math.acos(Math.sin(Math.toRadians(inLatitude))
                * Math.sin(Math.toRadians(lat)) + Math.cos(Math.toRadians(inLatitude))
                * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(inLongtitude) - Math.toRadians(lon))) * EARTH_RADIUS_IN_METERS;
        return dist <= radius;
    }
}

