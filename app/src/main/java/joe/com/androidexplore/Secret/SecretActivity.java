package joe.com.androidexplore.Secret;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import joe.com.androidexplore.R;
import joe.com.androidexplore.Secret.activity.LoginActivity;
import joe.com.androidexplore.Secret.activity.TimelineActivity;

/**
 * Created by JOE on 2016/7/19.
 */
public class SecretActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_secret);

        String token = Config.getCachedToken(this);
        if(token != null) {
            Intent intent = new Intent(this, TimelineActivity.class);
            intent.putExtra(Config.KEY_TOKEN, token);
            startActivity(intent);

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    }
}
