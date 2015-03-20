package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class TabItemView extends BaseFrameLayout {

    @InjectView(R.id.tab_icon)
    public ImageView tabIcon;

//    @InjectView(R.id.tab_title)
//    public CustomTextView tabTitle;

    public TabItemView(Context context) {
        super(context);
    }

    public TabItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.tabhost_indicator;
    }
}
