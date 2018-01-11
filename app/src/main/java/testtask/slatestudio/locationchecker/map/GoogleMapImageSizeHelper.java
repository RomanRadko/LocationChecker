package testtask.slatestudio.locationchecker.map;

/**
 * A helper class for calculating the best suitable image sizes to request from Google's static-map
 * API, given the size of the hosting image view.
 *
 * @author Roman
 * @since 11.01.2017.
 */
class GoogleMapImageSizeHelper {

    private static final int GOOGLE_MAPS_MAX_SIZE = 640;
    private static final float CROP_RATIO = 0.58f;

    private int mWidth;
    private int mHeight;
    private int mScale;

    GoogleMapImageSizeHelper() {
    }

    void calcSizes(int inWidth, int inHeight) {
        float width = inWidth;
        float height = inHeight;
        mScale = 2;
        if (width > GOOGLE_MAPS_MAX_SIZE || height > GOOGLE_MAPS_MAX_SIZE) {
            float widthShrinkRatio = width / (float) GOOGLE_MAPS_MAX_SIZE;
            float heightShrinkRatio = height / (float) GOOGLE_MAPS_MAX_SIZE;
            if (widthShrinkRatio > heightShrinkRatio) {
                width = GOOGLE_MAPS_MAX_SIZE;
                height /= widthShrinkRatio;
            } else {
                height = GOOGLE_MAPS_MAX_SIZE;
                width /= heightShrinkRatio;
            }
        }
        mHeight = Math.round(height * CROP_RATIO);
        mWidth = Math.round(width * CROP_RATIO);
    }

    int getWidth() {
        return mWidth;
    }

    int getHeight() {
        return mHeight;
    }

    int getScale() {
        return mScale;
    }
}
