package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 16/03/15.
 */
public class NavigationDrawerItemView extends BaseFrameLayout {

    @InjectView(R.id.menu_item_icon)
    public ImageView mIcon;

    @InjectView(R.id.menu_item_title)
    public CustomTextView mTitle;

    public NavigationDrawerItemView(Context context) {
        super(context);
    }

    public NavigationDrawerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_navigation_drawer_item;
    }
}
