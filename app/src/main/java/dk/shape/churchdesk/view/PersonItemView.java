package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by chirag on 13/02/2017.
 */
public class PersonItemView extends BaseFrameLayout {

    @InjectView(R.id.divider)
    public View mDivider;

    @InjectView(R.id.event_color)
    public View mPersonColor;

    @InjectView(R.id.checkbox_select)
    public CheckBox mSelectCheckBox;

    @InjectView(R.id.event_title)
    public CustomTextView mName;

    @InjectView(R.id.absence_icon)
    public ImageView mAbsenceIcon;

    @InjectView(R.id.event_location_icon)
    public ImageView mLocationIcon;

    @InjectView(R.id.event_location)
    protected CustomTextView mEmail;

    @InjectView(R.id.event_location_wrapper)
    protected LinearLayout mEmailWrapper;

    @InjectView(R.id.content_view)
    public LinearLayout mContentView;

    public PersonItemView(Context context) {
        super(context);
    }

    public PersonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_event_item;
    }

    public void setEmail(String email) {
        if (email == null || email.isEmpty())
            mEmailWrapper.setVisibility(INVISIBLE);
        else{
            mLocationIcon.setVisibility(GONE);
            mEmail.setText(email);}
    }
}