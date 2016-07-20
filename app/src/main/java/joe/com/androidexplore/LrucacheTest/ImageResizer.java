package joe.com.androidexplore.LrucacheTest;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileDescriptor;

/**
 * Created by JOE on 2016/7/19.
 */
public class ImageResizer {

    public static final String TAG = "ImageResizer";

    public ImageResizer() {
    }

    public Bitmap decodeSampleBitmapFromResource(Resources res, int resId,
                                                 int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd,
                                                        int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if(reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        final int height = options.outHeight;
        final int width = options.outWidth;
        int insampleSize = 1;

        if(height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            if((halfHeight / insampleSize) > reqHeight
                    && (halfWidth / insampleSize) > reqWidth) {
                insampleSize *= 2;
            }
        }
        return insampleSize;
    }


}
