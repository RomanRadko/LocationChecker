package testtask.slatestudio.locationchecker.tracking;

import testtask.slatestudio.locationchecker.tracking.gps.GPSPoint;

/**
 * @author Roman
 * @since 1/11/2018.
 */

public class Status {

    private boolean inGeoRange;
    private boolean inRange;
    private boolean isInWifiRange;
    private GPSPoint currentLocation;
    private GPSPoint targetLocation;
    private int radius;
    private String currentWifiName;

    private Status() {
    }

    public boolean isInRange() {
        return inRange;
    }

    public boolean isInWifiRange() {
        return isInWifiRange;
    }

    public boolean isInGeoRange() {
        return inGeoRange;
    }

    public GPSPoint getCurrentLocation() {
        return currentLocation;
    }

    public GPSPoint getTargetLocation() {
        return targetLocation;
    }

    public int getRadius() {
        return radius;
    }

    static Builder newBuilder() {
        return new Status().new Builder();
    }

    public class Builder {

        private Builder() {
        }

        Builder setInRange(boolean inRange) {
            Status.this.inRange = inRange;
            return this;
        }

        Builder setInWifiRange(boolean inWifiRange) {
            Status.this.isInWifiRange = inWifiRange;
            return this;
        }

        Builder setInGeoRange(boolean inGeoRange) {
            Status.this.inGeoRange = inGeoRange;
            return this;
        }

        Builder setCurrentLocation(GPSPoint currentLocation) {
            Status.this.currentLocation = currentLocation;
            return this;
        }

        Builder setTargetLocation(GPSPoint targetLocation) {
            Status.this.targetLocation = targetLocation;
            return this;
        }

        Builder setCurrentWifiName(String currentWifiName) {
            Status.this.currentWifiName = currentWifiName;
            return this;
        }

        public Builder setRadius(int radius) {
            Status.this.radius = radius;
            return this;
        }

        Status build() {
            return Status.this;
        }

    }
}
