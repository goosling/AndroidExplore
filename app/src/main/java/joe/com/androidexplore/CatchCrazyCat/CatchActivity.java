package joe.com.androidexplore.CatchCrazyCat;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by JOE on 2016/7/18.
 */
public class CatchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Playground(this));
    }
}
