package dk.shape.churchdesk.viewmodel;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import dk.shape.churchdesk.BaseActivity;
import dk.shape.churchdesk.BaseLoggedInActivity;
import dk.shape.churchdesk.NewMessageActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.Tag;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.util.CalendarUtils;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.EventDetailsMultiItemView;
import dk.shape.churchdesk.view.MultiSelectDialog;
import dk.shape.churchdesk.view.MultiSelectListItemView;
import dk.shape.churchdesk.view.PersonDetailsView;
import dk.shape.churchdesk.widget.CustomTextView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by chirag on 23/02/2017.
 */
public class PersonDetailsViewModel extends ViewModel<PersonDetailsView> {

    Person mPerson;
    User mUser;
    Site mSite;
    PersonDetailsView mPersonDetailsView;
    Context mContext;
    DatabaseUtils mDatabase;
    String mResponse;

    public PersonDetailsViewModel(User mCurrentUser, Person person) {
        System.out.println(person.mPeopleId);

        this.mPerson = person;
        this.mUser = mCurrentUser;
    }

    @Override
    public void bind(PersonDetailsView personDetailsView) {
        mPersonDetailsView = personDetailsView;
        mContext = personDetailsView.getContext();
        mDatabase = DatabaseUtils.getInstance();

        //Insert data
        insertData();
        mPersonDetailsView.mTagsLayout.setOnClickListener(mTagsClickListener);
        mPersonDetailsView.mPhoneLayout.setOnClickListener(mMobilePhoneListener);
        mPersonDetailsView.mHomePhoneLayout.setOnClickListener(mHomePhoneListener);
        mPersonDetailsView.mWorkPhoneLayout.setOnClickListener(mWorkPhoneListener);
        mPersonDetailsView.mEmailLayout.setOnClickListener(mEmailListener);
    }

    private void insertData() {
        int visibility;
        if (mPerson.mEmail == null || mPerson.mEmail.isEmpty()) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mPersonDetailsView.mEmail.setText(mPerson.mEmail);
        }
        mPersonDetailsView.mEmail.setVisibility(visibility);
        mPersonDetailsView.mEmailLayout.setVisibility(visibility);

        if (mPerson.mFullName == null || mPerson.mFullName.isEmpty()) {

        } else {
            mPersonDetailsView.mPersonProfileName.setText(mPerson.mFullName);
        }

        if (mPerson.mContact.get("phone") == null || mPerson.mContact.get("phone").isEmpty()) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mPersonDetailsView.mMobileNumber.setText(mPerson.mContact.get("phone"));
        }
        mPersonDetailsView.mEmailPhoneSeparator.setVisibility(visibility);
        mPersonDetailsView.mMobileNumber.setVisibility(visibility);
        mPersonDetailsView.mPhoneLayout.setVisibility(visibility);

        if (mPerson.mContact.get("homePhone") == null || mPerson.mContact.get("homePhone").isEmpty()) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mPersonDetailsView.mHomePhone.setText(mPerson.mContact.get("homePhone"));
        }
        mPersonDetailsView.mMobileHomeSeparator.setVisibility(visibility);
        mPersonDetailsView.mHomePhone.setVisibility(visibility);
        mPersonDetailsView.mHomePhoneLayout.setVisibility(visibility);

        if (mPerson.mContact.get("workPhone") == null || mPerson.mContact.get("workPhone").isEmpty()) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mPersonDetailsView.mWorkPhone.setText(mPerson.mContact.get("workPhone"));
        }
        mPersonDetailsView.mHomeWorkSeparator.setVisibility(visibility);
        mPersonDetailsView.mWorkPhone.setVisibility(visibility);
        mPersonDetailsView.mWorkPhoneLayout.setVisibility(visibility);

        if (mPerson.mContact.get("street") == null || mPerson.mContact.get("street").isEmpty()) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mPersonDetailsView.mAddress.setText(mPerson.mContact.get("street"));
        }
        mPersonDetailsView.mGenderAddressSeparator.setVisibility(visibility);
        mPersonDetailsView.mAddress.setVisibility(visibility);
        mPersonDetailsView.mAddressLayout.setVisibility(visibility);

        if (mPerson.mContact.get("city") == null || mPerson.mContact.get("city").isEmpty()) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mPersonDetailsView.mCity.setText(mPerson.mContact.get("city"));
        }
        mPersonDetailsView.mAddressCitySeparator.setVisibility(visibility);
        mPersonDetailsView.mCity.setVisibility(visibility);
        mPersonDetailsView.mCityLayout.setVisibility(visibility);

        if (mPerson.mContact.get("zipcode") == null || mPerson.mContact.get("zipcode").isEmpty()) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mPersonDetailsView.mPostalCode.setText(mPerson.mContact.get("zipcode"));
        }
        mPersonDetailsView.mCityPostalSeparator.setVisibility(visibility);
        mPersonDetailsView.mPostalCode.setVisibility(visibility);
        mPersonDetailsView.mPostalCodeLayout.setVisibility(visibility);

        if (mPerson.mOccupation == null || mPerson.mOccupation.isEmpty()) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mPersonDetailsView.mJobTitle.setText(mPerson.mOccupation);
        }
        mPersonDetailsView.mWorkJobSeparator.setVisibility(visibility);
        mPersonDetailsView.mJobTitle.setVisibility(visibility);
        mPersonDetailsView.mJobLayout.setVisibility(visibility);

        if (mPerson.mGender == null || mPerson.mGender.isEmpty()) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            if (mPerson.mGender.equals("male"))
                mPersonDetailsView.mGender.setText(mContext.getString(R.string.gender_male));
            if (mPerson.mGender.equals("female"))
                mPersonDetailsView.mGender.setText(mContext.getString(R.string.gender_female));
        }
        mPersonDetailsView.mRegisteredGenderSeparator.setVisibility(visibility);
        mPersonDetailsView.mGender.setVisibility(visibility);
        mPersonDetailsView.mGenderLayout.setVisibility(visibility);

        if (mPerson.mTags == null || mPerson.mTags.size() == 0) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            mPersonDetailsView.mTagsView.setText(String.valueOf(mPerson.mTags.size()));
        }
        mPersonDetailsView.mTagsView.setVisibility(visibility);
        mPersonDetailsView.mTagsLayout.setVisibility(visibility);
        mPersonDetailsView.mTagsBorder.setVisibility(visibility);

        if (mPerson.mPictureUrl.get("url") != null) {
            Picasso.with(mContext)
                    .load(mPerson.mPictureUrl.get("url"))
                    .into(mPersonDetailsView.mPersonProfileImage);
        } else {
            Picasso.with(mContext)
                    .load(R.drawable.user_default)
                    .into(mPersonDetailsView.mPersonProfileImage);
        }

        insertTimeString();


        mPersonDetailsView.mLayout.setVisibility(View.VISIBLE);
    }

    private void insertTimeString() {
        //set time of event
        String registered;

        TimeZone tz = TimeZone.getDefault();
        Calendar registeredOn = Calendar.getInstance();
        registeredOn.setTimeInMillis(mPerson.mRegistered.getTime() + tz.getOffset(mPerson.mRegistered.getTime()));
        String[] months = mContext.getResources().getStringArray(R.array.months);
        String[] weekdays = mContext.getResources().getStringArray(R.array.weekdays);
        registered = CalendarUtils.checkNumber(registeredOn.get(Calendar.DATE)) + " "
                + months[registeredOn.get(Calendar.MONTH)].substring(0, 3) + " " + registeredOn.get(Calendar.YEAR);

        mPersonDetailsView.mRegistered.setText(registered);
        int visibility;
        if (mPerson.mBirthday == null) {
            visibility = View.GONE;
        } else {
            visibility = View.VISIBLE;
            String birthdayString;
            Calendar birthday = Calendar.getInstance();
            birthday.setTimeInMillis(mPerson.mBirthday.getTime() + tz.getOffset(mPerson.mBirthday.getTime()));
            birthdayString = CalendarUtils.checkNumber(birthday.get(Calendar.DATE)) + " "
                    + months[birthday.get(Calendar.MONTH)].substring(0, 3) + " " + birthday.get(Calendar.YEAR);
            mPersonDetailsView.mBirthday.setText(birthdayString);
        }
        mPersonDetailsView.mJobBirthSeparator.setVisibility(visibility);
        mPersonDetailsView.mBirthday.setVisibility(visibility);
        mPersonDetailsView.mBirthdayLayout.setVisibility(visibility);
    }

    private LinearLayout.OnClickListener mMobilePhoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            makeCall(mPerson.mContact.get("phone"));
        }
    };

    private LinearLayout.OnClickListener mHomePhoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            makeCall(mPerson.mContact.get("homePhone"));
        }
    };

    private LinearLayout.OnClickListener mWorkPhoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            makeCall(mPerson.mContact.get("workPhone"));
        }
    };

    private LinearLayout.OnClickListener mEmailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, NewMessageActivity.class);
            intent.putExtra("EXTRA_MESSAGE_TYPE", "email");
            intent.putExtra("personId", mPerson.mPeopleId);
            mContext.startActivity(intent);
        }
    };

    private void makeCall(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            mContext.startActivity(intent);
            return;
        }

    }
    private LinearLayout.OnClickListener mTagsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new TagsListAdapter(), R.string.person_select_tags);
            dialog.showCancelButton(false);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                }
            });
            dialog.setOnOKClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };
    private class TagsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPerson.mTags != null ? mPerson.mTags.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mPerson.mTags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Tag tag = mPerson.mTags.get(position);

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            if(tag != null) {
                view.mItemTitle.setText(tag.mTagName);
                view.mItemDot.setVisibility(View.INVISIBLE);
            } else {
                view.mItemDot.setVisibility(View.INVISIBLE);
            }
            view.mItemSelected.setVisibility(View.GONE);
            return view;
        }
    }
}

