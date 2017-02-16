package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by chirag on 16/02/2017.
 */
public class SegmentItemView extends BaseFrameLayout {

    @InjectView(R.id.divider)
    public View mDivider;

    @InjectView(R.id.event_color)
    public View mSegmentColor;

    @InjectView(R.id.event_title)
    public CustomTextView mName;

    @InjectView(R.id.absence_icon)
    public ImageView mAbsenceIcon;

    @InjectView(R.id.content_view)
    public LinearLayout mContentView;

    @InjectView(R.id.event_location_wrapper)
    public LinearLayout mLocationWrapper;

    public SegmentItemView(Context context) {
        super(context);
    }


    public SegmentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_event_item;
    }

}
