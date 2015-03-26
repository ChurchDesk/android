package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import butterknife.InjectView;
import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class MessageFragmentView extends BaseFrameLayout {

    @InjectView(R.id.message_list)
    public ListView mMessageList;

    public MessageFragmentView(Context context) {
        super(context);
    }

    public MessageFragmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_messages;
    }
}
