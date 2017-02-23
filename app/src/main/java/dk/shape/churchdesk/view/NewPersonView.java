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
 * Created by chirag on 22/02/2017.
 */
public class NewPersonView extends BaseFrameLayout {

    @InjectView(R.id.person_first_name)
    public LinearLayout mFirstName;

    @InjectView(R.id.person_first_name_chosen)
    public MaterialEditText mFirstNameChosen;

    @InjectView(R.id.person_last_name)
    public LinearLayout mLastName;

    @InjectView(R.id.person_last_name_chosen)
    public MaterialEditText mLastNameChosen;

    @InjectView(R.id.person_email)
    public LinearLayout mEmail;

    @InjectView(R.id.person_email_chosen)
    public SwitchCompat mPersonEmailChosen;

    @InjectView(R.id.person_mobile_phone)
    public LinearLayout mMobilePhone;

    @InjectView(R.id.person_mobile_phone_chosen)
    public CustomTextView mMobilePhoneChosen;

    @InjectView(R.id.person_home_phone)
    public LinearLayout mHomePhone;

    @InjectView(R.id.person_home_phone_chosen)
    public CustomTextView mHomePhoneChosen;

    @InjectView(R.id.person_work_phone)
    public LinearLayout mWorkPhone;

    @InjectView(R.id.person_work_phone_chosen)
    public CustomTextView mWorkPhoneChosen;

    @InjectView(R.id.person_job_title)
    public LinearLayout mJobTitle;

    @InjectView(R.id.person_job_title_chosen)
    public CustomTextView mJobTitleChosen;

    @InjectView(R.id.person_birthday)
    public LinearLayout mBirthday;

    @InjectView(R.id.person_birthday_chosen)
    public MaterialEditText mBirthdayChosen;

    @InjectView(R.id.person_gender)
    public LinearLayout mGender;

    @InjectView(R.id.person_gender_chosen)
    public CustomTextView mGenderChosen;

    @InjectView(R.id.person_address)
    public LinearLayout mAddress;

    @InjectView(R.id.person_address_chosen)
    public CustomTextView mAddressChosen;

    @InjectView(R.id.person_city)
    public LinearLayout mCity;

    @InjectView(R.id.person_city_chosen)
    public MaterialEditText mCityChosen;

    @InjectView(R.id.person_postal_code)
    public LinearLayout mPostalCode;

    @InjectView(R.id.person_postal_code_chosen)
    public MaterialEditText mPostalCodeChosen;

    @InjectView(R.id.person_tags)
    public LinearLayout mTags;

    @InjectView(R.id.person_tags_chosen)
    public MaterialEditText mTagsChosen;

    public NewPersonView(Context context) {
        super(context);
    }

    public NewPersonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_new_person;
    }
}
