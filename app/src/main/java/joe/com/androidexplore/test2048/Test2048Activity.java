package joe.com.androidexplore.test2048;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import joe.com.androidexplore.R;

/**
 * Created by JOE on 2016/7/18.
 */
public class Test2048Activity extends Activity {

    private int score = 0;
    private TextView tvScore;

    public static Test2048Activity activity = null;

    public Test2048Activity() {
        activity = this;
    }

    public static Test2048Activity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_2048);

        tvScore = (TextView)findViewById(R.id.tv_score);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void clearScore() {
        score = 0;
        showScore();
    }

    public void showScore() {
        tvScore.setText(score + "");
    }

    public void addScore(int s) {
        score += s;
        showScore();
    }
}
