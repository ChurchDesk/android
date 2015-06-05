package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class EventItemView extends BaseFrameLayout {

    @InjectView(R.id.divider)
    public View mDivider;

    @InjectView(R.id.event_color)
    public View mEventColor;

    @InjectView(R.id.event_title)
    public CustomTextView mEventTitle;

    @InjectView(R.id.event_time)
    public CustomTextView mEventTime;

    @InjectView(R.id.event_site)
    public CustomTextView mEventSite;

    @InjectView(R.id.event_location)
    protected CustomTextView mEventLocation;

    @InjectView(R.id.event_location_wrapper)
    protected LinearLayout mEventLocationWrapper;

    @InjectView(R.id.content_view)
    public LinearLayout mContentView;

    public EventItemView(Context context) {
        super(context);
    }

    public EventItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_event_item;
    }

    public void setLocation(String location) {
        if (location == null || location.isEmpty())
            mEventLocationWrapper.setVisibility(INVISIBLE);
        else
            mEventLocation.setText(location);
    }
}
