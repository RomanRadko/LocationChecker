package testtask.slatestudio.locationchecker.geotracking;

/**
 * @author Roman
 * @since 1/9/2018.
 */
public interface GPSCallback<T> {

    void update(T t);
}