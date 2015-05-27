package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by root on 5/27/15.
 */
public class MessageHeaderView extends BaseFrameLayout {

    @InjectView(R.id.author_image)
    public CircleImageView mAuthorImage;

    @InjectView(R.id.author_name)
    public CustomTextView mAuthorName;

    @InjectView(R.id.time_ago)
    public CustomTextView mTimeAgo;

    @InjectView(R.id.group_title)
    public CustomTextView mGroupTitle;

    @InjectView(R.id.site_title)
    public CustomTextView mSiteTitle;

    @InjectView(R.id.message_title)
    public CustomTextView mMessageTitle;

    @InjectView(R.id.message_body)
    public CustomTextView mMessageBody;

    public MessageHeaderView(Context context) {
        super(context);
    }

    public MessageHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.message_header_view;
    }
}
