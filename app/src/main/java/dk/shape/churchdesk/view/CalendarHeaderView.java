package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by root on 6/3/15.
 */
public class CalendarHeaderView extends BaseFrameLayout {

    @InjectView(R.id.event_day_text)
    public CustomTextView mEventDayText;

    @InjectView(R.id.event_day_num)
    public CustomTextView mEventDayNum;

    @InjectView(R.id.holyday)
    public CustomTextView mHolyDay;

    public CalendarHeaderView(Context context) {
        super(context);
    }

    public CalendarHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_calendar_header_view;
    }
}
