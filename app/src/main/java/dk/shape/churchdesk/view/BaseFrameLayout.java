package dk.shape.churchdesk.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import butterknife.ButterKnife;

/**
 * Created by steffenkarlsson on 16/03/15.
 */
public abstract class BaseFrameLayout extends FrameLayout {

    public BaseFrameLayout(Context context) {
        super(context);
        init();
    }

    public BaseFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), getLayoutResource(), this);
        ButterKnife.inject(this);
    }

    @LayoutRes
    protected abstract int getLayoutResource();

}