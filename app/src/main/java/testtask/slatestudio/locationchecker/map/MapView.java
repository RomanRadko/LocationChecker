package testtask.slatestudio.locationchecker.map;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

import testtask.slatestudio.locationchecker.App;
import testtask.slatestudio.locationchecker.R;
import testtask.slatestudio.locationchecker.tracking.gps.GPSPoint;

/**
 * @author Roman
 * @since 11.01.2017.
 */
public class MapView extends FrameLayout {

    private static final String TAG = MapView.class.getSimpleName();

    private static final int DEFAULT_ZOOM_LEVEL = 15;
    private static final int GOOGLE_WATERMARKS_HEIGHT_PIXELS = 52;
    private static final double POINTS_DIF_DELTA = 0.001;

    protected Uri mMapUrl;
    protected boolean mMapWasLoaded = false;

    protected ImageView mMapImage;

    public MapView(Context context) {
        super(context);
        init(context);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMapImage = findViewById(R.id.map_card_map);
    }

    public void setPoints(GPSPoint currentLocation, GPSPoint newLocation, int radius) {
        mMapWasLoaded = false;
        if (currentLocation == null || newLocation == null) {
            return;
        }
        mMapUrl = getMapUrl(currentLocation, newLocation, radius);
        if (isSubstantiallyChanged(currentLocation, newLocation)) {
            loadStaticMap(mMapUrl);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        loadStaticMap(mMapUrl);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.card_map, this, true);
    }

    private void loadStaticMap(Uri mapUri) {
        if (mMapWasLoaded) {
            return;
        }

        if (mapUri == null) {
            return;
        }

        int rawWidth = mMapImage.getMeasuredWidth();
        int rawHeight = mMapImage.getMeasuredHeight();
        if (rawWidth <= 0 || rawHeight <= 0) {
            return;
        }

        mMapWasLoaded = true;
        rawHeight += (GOOGLE_WATERMARKS_HEIGHT_PIXELS * 2);

        GoogleMapImageSizeHelper apiImageSizeHelper = new GoogleMapImageSizeHelper();
        apiImageSizeHelper.calcSizes(rawWidth, rawHeight);

        mapUri = mapUri.buildUpon()
                .appendQueryParameter("size", "" + apiImageSizeHelper.getWidth() + "x" + apiImageSizeHelper.getHeight())
                .appendQueryParameter("scale", String.valueOf(apiImageSizeHelper.getScale()))
                .build();

        String mapUrl = mapUri.toString();
        Log.d(TAG, "Fetching static map for a " + rawWidth + "x" + rawHeight + " view, url=" + mapUrl);

        Picasso.with(App.getInstance())
                .load(mapUri)
                .resize(rawWidth, rawHeight)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(mMapImage);
    }

    private Uri getMapUrl(GPSPoint currentLocation, GPSPoint newLocation, int radius) {
        String latLonCurrent = currentLocation.getLat() + "," + currentLocation.getLon();
        String latLonNew = newLocation.getLat() + "," + newLocation.getLon();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("staticmap")
                .appendQueryParameter("radius", String.valueOf(radius))
                .appendQueryParameter("center", latLonCurrent)
                .appendQueryParameter("addr", latLonNew)
                .appendQueryParameter("zoom", Integer.toString(DEFAULT_ZOOM_LEVEL))
                .appendQueryParameter("language", Locale.getDefault().getLanguage())
                .appendQueryParameter("markers", "color:red|" + latLonCurrent)
                .appendQueryParameter("markers", "color:yellow|" + latLonNew);

        return builder.build();
    }

    private boolean isSubstantiallyChanged(GPSPoint currentLocation, GPSPoint newLocation) {
        return Math.abs(currentLocation.getLat() - newLocation.getLat()) > POINTS_DIF_DELTA
                || Math.abs(currentLocation.getLon() - newLocation.getLon()) > POINTS_DIF_DELTA;
    }

}
