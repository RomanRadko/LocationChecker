package testtask.slatestudio.locationchecker;

import org.junit.Before;
import org.junit.Test;

import testtask.slatestudio.locationchecker.tracking.LocationTracker;
import testtask.slatestudio.locationchecker.tracking.gps.GPSPoint;

import static org.junit.Assert.assertEquals;

/**
 * @author Roman
 * @since 1/12/2018.
 */

public class LocationTrackerTest {

    private LocationTracker tracker;

    @Before
    public void setUp() {
        tracker = new LocationTracker();
    }

    @Test
    public void testWifiNamePositive() throws Exception {
        tracker.setConnectedNetworkName("TEST");
        tracker.setTargetNetworkName("TEST");
        assertEquals("Location should be in range, when wifi names are equal.", true, tracker.isInRange());
    }

    @Test
    public void testWifiNameNegative() throws Exception {
        tracker.setConnectedNetworkName("TesT");
        tracker.setTargetNetworkName("tEsT");
        assertEquals("Location should not be in range, when wifi names are not equal.", false, tracker.isInRange());
    }

    @Test
    public void testGPSRadiusLocationPositive() throws Exception {
        tracker.setTargetPoint(new GPSPoint(50.122, 34.523));
        tracker.setCurrentPoint(new GPSPoint(50.123, 34.522));
        tracker.setTargetRadius(500);
        assertEquals("Location should be in range with such radius.", true, tracker.isInRange());
    }

    @Test
    public void testGPSRadiusLocationNegative() throws Exception {
        tracker.setTargetPoint(new GPSPoint(50.122, 34.523));
        tracker.setCurrentPoint(new GPSPoint(50.123, 34.522));
        tracker.setTargetRadius(50);
        assertEquals("Location should not be in range with such radius.", false, tracker.isInRange());
    }

    @Test
    public void testGPSPointLocationNegative() throws Exception {
        tracker.setTargetPoint(new GPSPoint(50.122, 34.523));
        tracker.setCurrentPoint(new GPSPoint(40.123, 44.522));
        tracker.setTargetRadius(500);
        assertEquals("Location should not be in range with such radius and target point.", false, tracker.isInRange());
    }

    @Test
    public void testInRangeGPSPositive() throws Exception {
        tracker.setTargetPoint(new GPSPoint(50.122, 34.523));
        tracker.setCurrentPoint(new GPSPoint(50.123, 34.522));
        tracker.setTargetRadius(500);
        tracker.setConnectedNetworkName("TesT");
        tracker.setTargetNetworkName("tEsT");
        assertEquals("Location should be in range with such radius and target point, even if wifi is different.", true, tracker.isInRange());
    }

    @Test
    public void testInRangeWifiPositive() throws Exception {
        tracker.setTargetPoint(new GPSPoint(50.122, 34.523));
        tracker.setCurrentPoint(new GPSPoint(40.123, 44.522));
        tracker.setTargetRadius(500);
        tracker.setConnectedNetworkName("TEST");
        tracker.setTargetNetworkName("TEST");
        assertEquals("Location should be in range, as wifi in range.", true, tracker.isInRange());
    }

}
