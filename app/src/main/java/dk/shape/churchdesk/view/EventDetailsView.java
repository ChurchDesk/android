package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by Martin on 02/06/2015.
 */
public class EventDetailsView extends BaseFrameLayout {


    @InjectView(R.id.event_details_image)
    public ImageView mImage;

    @InjectView(R.id.event_details_title)
    public CustomTextView mTitle;

    @InjectView(R.id.event_details_group)
    public CustomTextView mGroup;

    @InjectView(R.id.event_details_parish)
    public CustomTextView mParish;

    @InjectView(R.id.event_details_time)
    public CustomTextView mTime;

    @InjectView(R.id.event_details_location)
    public CustomTextView mLocation;

    @InjectView(R.id.event_details_location_seek)
    public CustomTextView mLocationButton;

    //Categories mangler her

    @InjectView(R.id.event_details_attendance_layout)
    public LinearLayout mAttendanceButton;

    @InjectView(R.id.event_details_attendance)
    public CustomTextView mAttendance;



    @InjectView(R.id.event_details_internal_layout)
    public LinearLayout mInternalLayout;

    //en masse til res og users mangler her

    @InjectView(R.id.event_details_note_layout)
    public LinearLayout mNoteButton;

    @InjectView(R.id.event_details_note)
    public CustomTextView mNote;



    @InjectView(R.id.event_details_external_layout)
    public LinearLayout mExternalLayout;

    @InjectView(R.id.event_details_contributor)
    public CustomTextView mContributor;

    @InjectView(R.id.event_details_price)
    public CustomTextView mPrice;

    @InjectView(R.id.event_details_description_layout)
    public LinearLayout mDescriptionButton;

    @InjectView(R.id.event_details_description)
    public CustomTextView mDescription;



    @InjectView(R.id.event_details_visibility)
    public CustomTextView mVisibility;

    @InjectView(R.id.event_details_created_date)
    public CustomTextView mDateCreated;



    public EventDetailsView(Context context) {
        super(context);
    }

    public EventDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.event_details_view;
    }
}
