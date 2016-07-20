package joe.com.androidexplore.LrucacheTest;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JOE on 2016/7/19.
 */
public class ImageLoader {
    private static final String TAG = "ImageLoader";

    private static final int MESSAGE_POST_RESULT = 1;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;

    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private static final long KEEP_ALIVE = 10L;

    private static final int TAG_KEY_URI = 0; //R.id.imageloader_uri;

    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 50;

    private static final int IO_BUFFER_SIZE = 8 * 1024;

    private static final int DISK_CACHE_INDEX = 0;

    private boolean mIsDiskLruCacheCreated = false;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ImageLoader#" + mCount.getAndIncrement());
        }
    };

    /**
     * 线程池THREAD_POOL_EXECUTOR的实现
     */
    public static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(), sThreadFactory);

    /**
     * Handler直接采用主线程的looper来构造Handler对象，使得ImageLoader可以在非主线程中构造；
     * 为了解决View复用的问题，在给ImageView设置图片的时候，都会检查的URL有没有发生改变，如果发生改变则
     * 不再设置图片，即解决了列表错位的问题
     */
    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult)msg.obj;
            ImageView imageView = result.mImageView;
            imageView.setImageBitmap(result.bitmap);
            String uri = (String)imageView.getTag(TAG_KEY_URI);
            if(uri.equals(result.uri)) {
                imageView.setImageBitmap(result.bitmap);
            }else {
                Log.d(TAG, "set imageBitmap, but url has changed, ignored");
            }
        }
    };

    private static class LoaderResult {
        public ImageView mImageView;
        public String uri;

        public Bitmap bitmap;

        public LoaderResult(ImageView mImageView, String uri, Bitmap bitmap) {
            this.mImageView = mImageView;
            this.uri = uri;
            this.bitmap = bitmap;
        }
    }

    private Context mContext;
    private ImageResizer mImageResizer = new ImageResizer();
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;

    private ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
        File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
        if(!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }

        if(getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try{
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            }catch(IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if(getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    /**
     * load bitmap from cache or disk cache or network async, then bind imageview and bitmap
     * @param uri
     * @param imageView
     */
    public void bindBitmap(final String uri, final ImageView imageView) {
        bindBitmap(uri, imageView, 0, 0);
    }

    public void bindBitmap(final String uri, final ImageView imageView,
                           final int reqWidth, final int reqHeight) {
        imageView.setTag(TAG_KEY_URI, uri);
        final Bitmap bitmap = loadBitmapFromMemCache(uri);
        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap1 = loadBitMap(uri, reqWidth, reqHeight);
                if(bitmap != null) {
                    LoaderResult result = new LoaderResult(imageView, uri, bitmap1);
                    mMainHandler.obtainMessage(MESSAGE_POST_RESULT, result).sendToTarget();
                }
            }
        };
        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    public Bitmap loadBitMap(String uri, int reqWidth, int reqHeight) {
        Bitmap bitmap = loadBitmapFromMemCache(uri);
        if(bitmap != null) {
            return bitmap;
        }

        try{
            bitmap = loadBitmapFromDiskCache(uri, reqWidth, reqHeight);
            if(bitmap != null) {
                return bitmap;
            }
            bitmap = loadBitmapFromHttp(uri, reqWidth, reqHeight);
        }catch (IOException e) {
            e.printStackTrace();
        }

        if(bitmap == null && !mIsDiskLruCacheCreated) {
            bitmap = downloadBitmapFromUrl(uri);
        }

        return bitmap;
    }

    private Bitmap loadBitmapFromMemCache(String url) {
        final String key = hashKeyFormUrl(url);
        Bitmap bitmap = getBitmapFromMemoryCache(key);
        return bitmap;
    }

    private Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight) throws IOException{
        if(Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network from UI thread");
        }

        if(mDiskLruCache == null) {
            return null;
        }

        String key = hashKeyFormUrl(url);
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if(editor != null) {
            OutputStream os = editor.newOutputStream(DISK_CACHE_INDEX);
            if(downloadUrlToStream(url, os)) {
                editor.commit();
            }else {
                editor.abort();
            }
            mDiskLruCache.flush();
        }
        return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
    }

    public Bitmap loadBitmapFromDiskCache(String url, int reqWidth, int reqHeight) throws IOException{
        if(Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap from UI thread, it is not recommended");
        }

        if(mDiskLruCache == null) {
            return null;
        }

        Bitmap bitmap = null;
        String key = hashKeyFormUrl(url);
        DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
        if(snapshot != null) {
            FileInputStream fis = (FileInputStream)snapshot.getInputStream(DISK_CACHE_INDEX);
            FileDescriptor descriptor = fis.getFD();
            bitmap = mImageResizer.decodeSampledBitmapFromFileDescriptor(descriptor, reqWidth, reqHeight);
            if(bitmap != null) {
                addBitmapToMemoryCache(key, bitmap);
            }
        }
        return bitmap;
    }

    public boolean downloadUrlToStream(String url, OutputStream os) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try{
            final URL url1 = new URL(url);
            urlConnection = (HttpURLConnection)url1.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(os, IO_BUFFER_SIZE);

            int b;
            while((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "download bitmap failed");
        }finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            try{
                out.close();
                in.close();
            }catch (IOException e) {

            }

        }
        return false;
    }

    private Bitmap downloadBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;

        try{
            URL url1 = new URL(url);
            urlConnection = (HttpURLConnection)url1.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        }catch (final IOException e) {
            Log.e(TAG, "error in download bitmap: " + e);
        }finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            try{
                in.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private String hashKeyFormUrl(String url) {
        String cacheKey;
        try{
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(url.getBytes());
            cacheKey = bytesToHexString(messageDigest.digest());
        }catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i< bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if(hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    public File getDiskCacheDir(Context context, String uniqueName) {
        boolean externalStorageAvailable = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        final String cachePath;
        if(externalStorageAvailable) {
            cachePath = context.getExternalCacheDir().getPath();
        }else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private long getUsableSpace(File path) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long)stats.getBlockSize() * (long)stats.getAvailableBlocks();
    }



}
