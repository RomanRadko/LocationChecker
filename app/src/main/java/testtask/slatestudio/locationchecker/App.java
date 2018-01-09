package testtask.slatestudio.locationchecker;

import android.app.Application;

/**
 * @author Roman
 * @since 1/9/2018.
 */
public class App extends Application {

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}
