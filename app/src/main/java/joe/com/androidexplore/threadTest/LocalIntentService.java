package joe.com.androidexplore.threadTest;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by JOE on 2016/7/18.
 */
public class LocalIntentService extends IntentService {

    private static final String TAG = "IntentService";

    public LocalIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra("task_action");
        Log.d(TAG, "receive task :" + action);
        SystemClock.sleep(3000);
        if("joe.com.androidexplore.action.TASK1".equals(action)) {
            Log.d(TAG, "handle task: " + action);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
