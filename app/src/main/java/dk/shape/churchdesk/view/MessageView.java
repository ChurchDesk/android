package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomEditText;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class MessageView extends BaseFrameLayout {

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
