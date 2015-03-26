package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class MessageItemView extends BaseFrameLayout {

    @InjectView(R.id.group_title)
    public CustomTextView mGroupTitle;

    @InjectView(R.id.site_title)
    public CustomTextView mSiteTitle;

    @InjectView(R.id.time_ago)
    public CustomTextView mTimeAgo;

    @InjectView(R.id.username)
    public CustomTextView mUsername;

    @InjectView(R.id.subject)
    public CustomTextView mSubject;

    public MessageItemView(Context context) {
        super(context);
    }

    public MessageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_message_item;
    }
}
