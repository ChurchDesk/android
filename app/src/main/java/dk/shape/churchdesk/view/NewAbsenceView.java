package dk.shape.churchdesk.view;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by chirag on 21/01/16.
 */
public class NewAbsenceView extends BaseFrameLayout {

    @InjectView(R.id.event_new_title)
    public LinearLayout mTitle;

    @InjectView(R.id.event_new_title_chosen)
    public MaterialEditText mTitleChosen;



    @InjectView(R.id.event_new_time_allday)
    public LinearLayout mTimeAllday;

    @InjectView(R.id.event_new_time_allday_chosen)
    public SwitchCompat mTimeAlldayChosen;

    @InjectView(R.id.event_new_time_start)
    public LinearLayout mTimeStart;

    @InjectView(R.id.event_new_time_start_chosen)
    public CustomTextView mTimeStartChosen;

    @InjectView(R.id.event_new_time_end)
    public LinearLayout mTimeEnd;

    @InjectView(R.id.event_new_time_end_chosen)
    public CustomTextView mTimeEndChosen;



    @InjectView(R.id.event_new_site_parish)
    public LinearLayout mSiteParish;

    @InjectView(R.id.event_new_site_parish_chosen)
    public CustomTextView mSiteParishChosen;

    @InjectView(R.id.event_new_site_parish_group_seperator)
    public View mParishGroupSeperator;

    @InjectView(R.id.event_new_site_group)
    public LinearLayout mSiteGroup;

    @InjectView(R.id.event_new_site_group_chosen)
    public CustomTextView mSiteGroupChosen;

    @InjectView(R.id.event_new_site_category)
    public LinearLayout mSiteCategory;

    @InjectView(R.id.event_new_site_category_chosen)
    public CustomTextView mSiteCategoryChosen;



    @InjectView(R.id.event_new_location)
    public LinearLayout mLocation;

    @InjectView(R.id.event_new_location_chosen)
    public MaterialEditText mLocationChosen;



    @InjectView(R.id.event_new_booking_res)
    public LinearLayout mResources;

    @InjectView(R.id.event_new_booking_res_chosen)
    public CustomTextView mResourcesChosen;

    @InjectView(R.id.event_new_users)
    public LinearLayout mUsers;

    @InjectView(R.id.event_new_users_chosen)
    public CustomTextView mUsersChosen;



    @InjectView(R.id.event_new_internal_note)
    public LinearLayout mNote;

    @InjectView(R.id.event_new_internal_note_chosen)
    public MaterialEditText mNoteChosen;




    @InjectView(R.id.event_new_description)
    public LinearLayout mDescription;

    @InjectView(R.id.event_new_description_chosen)
    public MaterialEditText mDescriptionChosen;




    @InjectView(R.id.event_new_contributor)
    public LinearLayout mContributor;

    @InjectView(R.id.event_new_contributor_chosen)
    public MaterialEditText mContributorChosen;

    @InjectView(R.id.event_new_price)
    public LinearLayout mPrice;

    @InjectView(R.id.event_new_price_chosen)
    public MaterialEditText mPriceChosen;

    @InjectView(R.id.event_new_double_booking)
    public LinearLayout mAllowDoubleBooking;

    @InjectView(R.id.event_new_double_booking_chosen)
    public SwitchCompat mAllowDoubleBookingChosen;

    @InjectView(R.id.event_new_visibility)
    public LinearLayout mVisibility;

    @InjectView(R.id.event_new_visibility_chosen)
    public CustomTextView mVisibilityChosen;




    public NewAbsenceView(Context context) {
        super(context);
    }

    public NewAbsenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_new_event;
    }
}
