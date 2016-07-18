package joe.com.androidexplore.threadTest;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by JOE on 2016/7/17.
 */
public class MyThreadLocal {

    private static final String TAG = "MyThreadLocal";

    private ThreadLocal<Boolean> mBooleanThreadLocal = new ThreadLocal<Boolean>();

    /**
     * 在主线程中访问和设置
     */
    //mBooleanThreadLocal.set(true);
    private void testThread() {
        mBooleanThreadLocal.set(true);
        Log.d(TAG, "[ThreadLocal#main]mBooleanThreadLocal" + mBooleanThreadLocal.get());

        //在子线程中访问和设置
        new Thread("Thread#1") {
            @Override
            public void run() {
                mBooleanThreadLocal.set(false);
            }
        }.start();

        new Thread("Thread#2"){
            @Override
            public void run() {
                Looper.prepare();
                Handler handler = new Handler();
                Looper.loop();
            }
        }.start();
    }
}
