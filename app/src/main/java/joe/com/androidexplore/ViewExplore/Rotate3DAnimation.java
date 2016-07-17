package joe.com.androidexplore.ViewExplore;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by JOE on 2016/7/16.
 *
 * 自定义View动画，继承Animation类，重写initialize方法和applyTransformation方法
 * 在initialize中做一些初始化工作，在applyTransformation中做相应的矩阵变化即可，
 * 很多时候采用Camera简化矩阵变化的过程
 */
public class Rotate3DAnimation extends Animation {

    private final float fromDegrees;
    private final float toDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private final float mDepthZ;
    private final boolean mReverse;
    private Camera mCamera;

    public Rotate3DAnimation(float fromDegrees, float toDegrees, float mCenterX, float mCenterY, float mDepthZ, boolean mReverse) {
        this.fromDegrees = fromDegrees;
        this.toDegrees = toDegrees;
        this.mCenterX = mCenterX;
        this.mCenterY = mCenterY;
        this.mDepthZ = mDepthZ;
        this.mReverse = mReverse;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = this.fromDegrees;
        float degrees = fromDegrees + ((this.toDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;

        final Matrix matrix = t.getMatrix();

        camera.save();
        if(mReverse) {
            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
        }else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}
