package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by Martin on 04/06/2015.
 */
public class EventDetailsMultiItemView extends BaseFrameLayout {


    @InjectView(R.id.event_details_multi_category1)
    public CustomTextView mMultiCategory1;

    @InjectView(R.id.event_details_multi_category2)
    public CustomTextView mMultiCategory2;

    public EventDetailsMultiItemView(Context context) {
        super(context);
    }

    public EventDetailsMultiItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_event_details_multiple;
    }
}
