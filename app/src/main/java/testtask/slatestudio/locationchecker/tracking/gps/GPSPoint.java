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

    public GPSPoint(double latitude, double longitude) {
        lat = latitude;
        lon = longitude;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "(" + lat + ", " + lon + ")";
    }

    public boolean isInRange(double inLatitude, double inLongtitude, int radius) {
        if (radius <= 0 || inLatitude < -90 || inLatitude > 90 || inLongtitude < -180 || inLongtitude > 180) {
            return false;
        }
        double dist = Math.acos(Math.sin(Math.toRadians(inLatitude))
                * Math.sin(Math.toRadians(lat)) + Math.cos(Math.toRadians(inLatitude))
                * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(inLongtitude) - Math.toRadians(lon))) * EARTH_RADIUS_IN_METERS;
        return dist <= radius;
    }
}

