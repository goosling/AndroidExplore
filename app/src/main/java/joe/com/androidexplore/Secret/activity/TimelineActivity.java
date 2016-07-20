package joe.com.androidexplore.Secret.activity;

import android.app.ListActivity;
import android.os.Bundle;

import joe.com.androidexplore.R;

/**
 * Created by JOE on 2016/7/19.
 */
public class TimelineActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secret_timeline_layout);

    }
}
