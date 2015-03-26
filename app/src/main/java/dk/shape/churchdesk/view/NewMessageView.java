package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;

import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class NewMessageView extends BaseFrameLayout {

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
}
