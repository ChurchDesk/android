package dk.shape.churchdesk.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomButton;
import dk.shape.churchdesk.widget.CustomEditText;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class MessageView extends BaseFrameLayout {

    @InjectView(R.id.btn_reply)
    public CustomButton mButtonReply;

    @InjectView(R.id.edit_reply)
    public CustomEditText mReply;

    @InjectView(R.id.comments_view)
    public RecyclerView mCommentsView;

    public MessageView(Context context) {
        super(context);
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_message;
    }

}