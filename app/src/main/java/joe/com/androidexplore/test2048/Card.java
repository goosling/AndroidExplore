package joe.com.androidexplore.test2048;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by JOE on 2016/7/18.
 */
public class Card extends FrameLayout {



    public Card(Context context) {
        super(context);

        label = new TextView(getContext());
        label.setTextSize(32);
        label.setGravity(Gravity.CENTER);
        label.setBackgroundColor(0x33ffffff);
        LayoutParams params = new LayoutParams(-1, -1);
        params.setMargins(10, 10, 0, 0);
        addView(label, params);

        setNum(0);
    }

    private int num = 0;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;

        if(num <= 0) {
            label.setText("");
        }else {
            label.setText(num + "");
        }

    }

    private TextView label;

    public boolean equals(Card card) {
        return getNum() == card.getNum();
    }
}
