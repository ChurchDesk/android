package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by root on 5/27/15.
 */
public class CommentView extends BaseFrameLayout {

    @InjectView(R.id.author_image)
    public CircleImageView mAuthorImage;

    @InjectView(R.id.author_name)
    public CustomTextView mAuthorName;

    @InjectView(R.id.ago)
    public CustomTextView mTimeAgo;

    @InjectView(R.id.comment_body)
    public CustomTextView mCommentBody;

    @InjectView(R.id.actionButton)
    public FrameLayout actionButton;

    public CommentView(Context context) {
        super(context);
    }

    public CommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_comment_view;
    }
}
