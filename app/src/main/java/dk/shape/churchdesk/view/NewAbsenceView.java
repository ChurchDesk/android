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
 * Created by chirag on 23/02/16.
 */
public class NewAbsenceView extends BaseFrameLayout {

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

    @InjectView(R.id.event_new_users)
    public LinearLayout mUsers;

    @InjectView(R.id.event_new_users_chosen)
    public CustomTextView mUsersChosen;



    @InjectView(R.id.absence_new_substitute)
    public LinearLayout mSubstitute;

    @InjectView(R.id.absence_new_substitute_chosen)
    public MaterialEditText mSubstituteChosen;

    @InjectView(R.id.absence_new_comments)
    public LinearLayout mComments;

    @InjectView(R.id.absence_new_comments_chosen)
    public MaterialEditText mCommentsChosen;

    public NewAbsenceView(Context context) {
        super(context);
    }

    public NewAbsenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_new_absence;
    }
}

