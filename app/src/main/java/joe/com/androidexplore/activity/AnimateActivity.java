package joe.com.androidexplore.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import joe.com.androidexplore.R;

/**
 * Created by JOE on 2016/7/16.
 */
public class AnimateActivity extends Activity implements View.OnClickListener{

    private Button mValueAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        mValueAnimator = (Button)findViewById(R.id.value_animator);
        mValueAnimator.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == mValueAnimator) {
            performAnimate();
        }
    }

    private void performAnimate() {
        ViewWrapper viewWrapper = new ViewWrapper(mValueAnimator);
        ObjectAnimator.ofInt(viewWrapper, "width", 500).setDuration(5000).start();
    }

    private static class ViewWrapper {
        private View mTarget;

        public ViewWrapper(View mTarget) {
            this.mTarget = mTarget;
        }

        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }
    }

}
