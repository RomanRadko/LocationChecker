package testtask.slatestudio.locationchecker.tracking.gps;

/**
 * @author Roman
 * @since 1/9/2018.
 */
public interface GPSCallback<T> {

    void update(T t);
}