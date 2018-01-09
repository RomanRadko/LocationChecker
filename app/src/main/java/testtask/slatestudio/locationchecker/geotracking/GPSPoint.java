package testtask.slatestudio.locationchecker.geotracking;

import java.math.BigDecimal;

/**
 * @author Roman
 * @since 1/9/2018.
 */
public class GPSPoint {

    private BigDecimal lat;
    private BigDecimal lon;

    GPSPoint(Double latitude, Double longitude) {
        this.lat = BigDecimal.valueOf(latitude);
        this.lon = BigDecimal.valueOf(longitude);
    }

    @Override
    public String toString() {
        return "(" + lat + ", " + lon + ")";
    }

    public boolean isInRange(Double inLatitude, Double inLongtitude, int radius) {
        //TODO:
        return false;
    }
}

