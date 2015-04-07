package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Date;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class InvitationItemView extends BaseFrameLayout {

    @InjectView(R.id.event_color)
    public View mEventColor;

    @InjectView(R.id.event_title)
    public CustomTextView mEventTitle;

    @InjectView(R.id.by_who_title)
    public CustomTextView mByWhoTitle;

    @InjectView(R.id.event_site)
    public CustomTextView mEventSite;

    @InjectView(R.id.event_created_ago)
    public CustomTextView mCreatedAgo;

    @InjectView(R.id.event_location)
    protected CustomTextView mEventLocation;

    @InjectView(R.id.event_time_wrapper)
    protected LinearLayout mEventTimeWrapper;

    @InjectView(R.id.event_time)
    public CustomTextView mEventTime;

    @InjectView(R.id.event_location_wrapper)
    protected LinearLayout mEventLocationWrapper;

    public InvitationItemView(Context context) {
        super(context);
    }

    public InvitationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_invitation_item;
    }

    public void setLocation(String location) {
        if (location == null || location.isEmpty())
            mEventLocationWrapper.setVisibility(INVISIBLE);
        else
            mEventLocation.setText(location);
    }

    public void setTime(Date start, Date end, boolean isAllDay) {
        if (start == null || end == null)
            mEventTimeWrapper.setVisibility(INVISIBLE);
        else
            mEventLocation.setText("FOO");
    }
}
