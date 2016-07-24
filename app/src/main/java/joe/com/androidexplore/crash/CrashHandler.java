package joe.com.androidexplore.crash;

import android.content.Context;
import android.os.Environment;

/**
 * Created by JOE on 2016/7/24.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    private static final boolean DEBUG = true;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath()
            + "/CrashTest/log/";
    private static final String FILE_NAME = "crash";

    private static final String FILE_NAME_SUFFIX = ".trace";

    private static CrashHandler sInstance = new CrashHandler();

    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

    }
}
