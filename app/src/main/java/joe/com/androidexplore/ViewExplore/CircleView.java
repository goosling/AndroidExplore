package joe.com.androidexplore.ViewExplore;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import joe.com.androidexplore.R;

/**
 * Created by JOE on 2016/7/15.
 *
 * 自定义View，继承View重写onDraw方法，采用这种方式需要自己支持wrap_content, 并且padding也需要自己处理
 */
public class CircleView extends View {

    private int mColor = Color.RED;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CircleView(Context context) {
        super(context);
        init();
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //因为attrs_circle_view中属性的存在，需要在构造器中对其进行处理
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mColor = a.getColor(R.styleable.CircleView_circle_color, Color.RED);
        a.recycle();
        init();
    }

    private void init() {
        mPaint.setColor(mColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if(widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, 200);
        }else if(widthMeasureSpec == MeasureSpec.AT_MOST) {
            setMeasuredDimension(200, heightSpecSize);
        } else {
            setMeasuredDimension(widthSpecSize, 200);
        }
    }

    /**
     * 重写onDraw时，需要对wrap_content和padding分别进行处理，不然默认是match_parent
     * wrap_content:指定一个wrap_content默认的宽、高即可，比如选择200dp作为默认宽高
     * padding：绘制的时候考虑padding即可
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*int width = getWidth();
        int height = getHeight();*/

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        final int paddingTop = getPaddingTop();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        int radius = Math.min(width, height) / 2;
        //canvas.drawCircle(width / 2, height / 2, radius, mPaint);
        canvas.drawCircle(paddingLeft + width / 2, paddingTop + height / 2, radius, mPaint);
    }
}
