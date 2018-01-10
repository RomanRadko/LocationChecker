package testtask.slatestudio.locationchecker;

import org.junit.Test;

import testtask.slatestudio.locationchecker.tracking.gps.GPSPoint;

import static org.junit.Assert.assertEquals;

/**
 * @author Roman
 * @since 10.01.2018.
 */
public class GPSPointValidationTest {

    @Test
    public void testNegativeLatitude() throws Exception {
        GPSPoint gpsPoint = new GPSPoint();
        boolean isInRange = gpsPoint.isInRange(100.0, 12.0, 12);
        assertEquals("Can't be in range, when latitude is outrange.", false, isInRange);
    }

    @Test
    public void testNegativeLongtitude() throws Exception {
        GPSPoint gpsPoint = new GPSPoint();
        boolean isInRange = gpsPoint.isInRange(12.0, 200.0, 12);
        assertEquals("Can't be in range, when longtitude is outrange.", false, isInRange);
    }

    @Test
    public void testNegativeRadius() throws Exception {
        GPSPoint gpsPoint = new GPSPoint();
        boolean isInRange = gpsPoint.isInRange(12.0, 12.0, -12);
        assertEquals("Can't be in range, when radius is negative.", false, isInRange);
    }

}
