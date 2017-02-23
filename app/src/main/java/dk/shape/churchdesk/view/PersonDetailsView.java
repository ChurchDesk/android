package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by chirag on 23/02/2017.
 */
public class PersonDetailsView extends BaseFrameLayout {
    @InjectView(R.id.person_details_layout)
    public LinearLayout mLayout;

    @InjectView(R.id.person_details_email)
    public CustomTextView mEmail;

    @InjectView(R.id.person_details_email_layout)
    public LinearLayout mEmailLayout;

    @InjectView(R.id.person_details_mobile)
    public CustomTextView mMobileNumber;

    @InjectView(R.id.person_details_phone_layout)
    public LinearLayout mPhoneLayout;

    @InjectView(R.id.person_details_home_phone)
    public CustomTextView mHomePhone;

    @InjectView(R.id.person_details_home_phone_layout)
    public LinearLayout mHomePhoneLayout;

    @InjectView(R.id.person_details_work_phone)
    public CustomTextView mWorkPhone;

    @InjectView(R.id.person_details_work_phone_layout)
    public LinearLayout mWorkPhoneLayout;

    @InjectView(R.id.person_details_job_title)
    public CustomTextView mJobTitle;

    @InjectView(R.id.person_details_job_layout)
    public LinearLayout mJobLayout;

    @InjectView(R.id.person_details_birthday)
    public CustomTextView mBirthday;

    @InjectView(R.id.person_details_birthday_layout)
    public LinearLayout mBirthdayLayout;

    @InjectView(R.id.person_details_registered)
    public CustomTextView mRegistered;

    @InjectView(R.id.person_details_gender)
    public CustomTextView mGender;

    @InjectView(R.id.person_details_gender_layout)
    public LinearLayout mGenderLayout;

    @InjectView(R.id.person_details_address)
    public CustomTextView mAddress;

    @InjectView(R.id.person_details_address_layout)
    public LinearLayout mAddressLayout;

    @InjectView(R.id.person_details_city)
    public CustomTextView mCity;

    @InjectView(R.id.person_details_city_layout)
    public LinearLayout mCityLayout;

    @InjectView(R.id.person_details_postal_code)
    public CustomTextView mPostalCode;

    @InjectView(R.id.person_details_postal_layout)
    public LinearLayout mPostalCodeLayout;

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
