package dk.shape.churchdesk.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
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

    int mIconRes, mPassiveIconRes;

    int mPassiveColor = getContext().getResources().getColor(R.color.navigation_drawer_unselected);

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

    public void setSelected(boolean isSelected) {
        int color = isSelected ? Color.WHITE : mPassiveColor;
        mTitle.setTextColor(color);

        Drawable icon = getResources().getDrawable(isSelected ? mIconRes : mPassiveIconRes);
        if (icon != null) {
            //icon.setColorFilter(new LightingColorFilter(color, color));
            mIcon.setImageDrawable(icon);
        }
    }

    public void setPassiveIconRes(int mPassiveIconRes) {
        this.mPassiveIconRes = mPassiveIconRes;
    }

    public void setIconRes(int mIconRes) {
        this.mIconRes = mIconRes;
    }
}
