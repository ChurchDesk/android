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
 * Created by chirag on 23/02/2017.
 */
public class PersonDetailsView extends BaseFrameLayout {

    @InjectView(R.id.person_profile_image)
    public CircleImageView mPersonProfileImage;

    @InjectView(R.id.person_profile_name)
    public CustomTextView mPersonProfileName;

    @InjectView(R.id.person_details_layout)
    public LinearLayout mLayout;

    @InjectView(R.id.person_details_email)
    public CustomTextView mEmail;

    @InjectView(R.id.person_details_email_layout)
    public LinearLayout mEmailLayout;

    @InjectView(R.id.person_details_email_phone_separator)
    public View mEmailPhoneSeparator;

    @InjectView(R.id.person_details_mobile)
    public CustomTextView mMobileNumber;

    @InjectView(R.id.person_details_phone_layout)
    public LinearLayout mPhoneLayout;

    @InjectView(R.id.person_details_mobile_home_separator)
    public View mMobileHomeSeparator;

    @InjectView(R.id.person_details_home_phone)
    public CustomTextView mHomePhone;

    @InjectView(R.id.person_details_home_phone_layout)
    public LinearLayout mHomePhoneLayout;

    @InjectView(R.id.person_details_home_work_separator)
    public View mHomeWorkSeparator;

    @InjectView(R.id.person_details_work_phone)
    public CustomTextView mWorkPhone;

    @InjectView(R.id.person_details_work_phone_layout)
    public LinearLayout mWorkPhoneLayout;

    @InjectView(R.id.person_details_work_job_separator)
    public View mWorkJobSeparator;

    @InjectView(R.id.person_details_job_title)
    public CustomTextView mJobTitle;

    @InjectView(R.id.person_details_job_layout)
    public LinearLayout mJobLayout;

    @InjectView(R.id.person_details_job_birthday_separator)
    public View mJobBirthSeparator;

    @InjectView(R.id.person_details_birthday)
    public CustomTextView mBirthday;

    @InjectView(R.id.person_details_birthday_layout)
    public LinearLayout mBirthdayLayout;

    @InjectView(R.id.person_details_birthday_registered_separator)
    public View mBirthRegisteredSeparator;

    @InjectView(R.id.person_details_registered)
    public CustomTextView mRegistered;

    @InjectView(R.id.person_details_registered_gender_separator)
    public View mRegisteredGenderSeparator;

    @InjectView(R.id.person_details_gender)
    public CustomTextView mGender;

    @InjectView(R.id.person_details_gender_layout)
    public LinearLayout mGenderLayout;

    @InjectView(R.id.person_details_gender_address_separator)
    public View mGenderAddressSeparator;

    @InjectView(R.id.person_details_address)
    public CustomTextView mAddress;

    @InjectView(R.id.person_details_address_layout)
    public LinearLayout mAddressLayout;

    @InjectView(R.id.person_details_address_city_separator)
    public View mAddressCitySeparator;

    @InjectView(R.id.person_details_city)
    public CustomTextView mCity;

    @InjectView(R.id.person_details_city_layout)
    public LinearLayout mCityLayout;

    @InjectView(R.id.person_details_city_postal_separator)
    public View mCityPostalSeparator;

    @InjectView(R.id.person_details_postal_code)
    public CustomTextView mPostalCode;

    @InjectView(R.id.person_details_postal_layout)
    public LinearLayout mPostalCodeLayout;

    @InjectView(R.id.person_details_tags_layout)
    public LinearLayout mTagsLayout;

    @InjectView(R.id.person_details_tags)
    public CustomTextView mTagsView;

    @InjectView(R.id.person_details_tags_border)
    public View mTagsBorder;

    public PersonDetailsView(Context context) {
        super(context);
    }

    public PersonDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.person_details_view;
    }
}
