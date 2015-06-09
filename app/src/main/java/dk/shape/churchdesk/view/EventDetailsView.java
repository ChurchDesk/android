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
 * Created by Martin on 02/06/2015.
 */
public class EventDetailsView extends BaseFrameLayout {

    @InjectView(R.id.event_details_layout)
    public LinearLayout mLayout;

    @InjectView(R.id.event_details_image)
    public ImageView mImage;

    @InjectView(R.id.event_details_image_group_seperator)
    public View mImageGroupSeperator;

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

    @InjectView(R.id.event_details_location_layout)
    public LinearLayout mLocationLayout;

    @InjectView(R.id.event_details_category_view)
    public LinearLayout mCategoryView;

    @InjectView(R.id.event_details_category_layout)
    public LinearLayout mCategoryLayout;

    @InjectView(R.id.event_details_categories_attendance_seperator)
    public View mCategoryAttendanceSeperator;

    @InjectView(R.id.event_details_attendance_layout)
    public LinearLayout mAttendanceButton;

    @InjectView(R.id.event_details_attendance)
    public CustomTextView mAttendance;



    @InjectView(R.id.event_details_internal_layout)
    public LinearLayout mInternalLayout;

    @InjectView(R.id.event_details_resources_layout)
    public LinearLayout mResourcesLayout;

    @InjectView(R.id.event_details_resources_view)
    public LinearLayout mResourcesView;

    @InjectView(R.id.event_details_users_layout)
    public LinearLayout mUsersLayout;

    @InjectView(R.id.event_details_res_users_seperator)
    public View mResUsersSeperator;

    @InjectView(R.id.event_details_users_view)
    public LinearLayout mUsersView;

    @InjectView(R.id.event_details_users_note_seperator)
    public View mUsersNoteSeperator;

    @InjectView(R.id.event_details_note_layout)
    public LinearLayout mNoteButton;

    @InjectView(R.id.event_details_note)
    public CustomTextView mNote;



    @InjectView(R.id.event_details_external_layout)
    public LinearLayout mExternalLayout;

    @InjectView(R.id.event_details_contributor)
    public CustomTextView mContributor;

    @InjectView(R.id.event_details_contributor_layout)
    public LinearLayout mContributorLayout;

    @InjectView(R.id.event_details_contributor_price_seperator)
    public View mContributorPriceSeperator;



    @InjectView(R.id.event_details_price)
    public CustomTextView mPrice;

    @InjectView(R.id.event_details_price_layout)
    public LinearLayout mPriceLayout;

    @InjectView(R.id.event_details_price_description_seperator)
    public View mPriceDescriptionSeperator;

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
