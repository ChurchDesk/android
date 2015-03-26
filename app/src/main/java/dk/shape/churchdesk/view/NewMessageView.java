package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class NewMessageView extends BaseFrameLayout {

    @InjectView(R.id.wrapper_site)
    protected LinearLayout mWrapperSite;

    @InjectView(R.id.site_title)
    public CustomTextView mSiteTitle;

    @InjectView(R.id.site_group_title)
    public CustomTextView mSiteGroupTitle;

    @InjectView(R.id.wrapper_no_site)
    public LinearLayout mWrapperNoSite;

    @InjectView(R.id.no_site_group_title)
    public CustomTextView mGroupTitle;

    @InjectView(R.id.wrapper_site_item)
    public LinearLayout mWrapperSiteItem;

    @InjectView(R.id.wrapper_group_item)
    public LinearLayout mWrapperGroupItem;

    public NewMessageView(Context context) {
        super(context);
    }

    public NewMessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_new_message;
    }

    public void setState(boolean isSingleUser) {
        mWrapperSite.setVisibility(isSingleUser ? GONE : VISIBLE);
        mWrapperNoSite.setVisibility(isSingleUser ? VISIBLE : GONE);
    }
}
