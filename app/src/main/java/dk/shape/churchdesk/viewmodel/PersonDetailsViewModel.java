package dk.shape.churchdesk.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import dk.shape.churchdesk.BaseActivity;
import dk.shape.churchdesk.BaseLoggedInActivity;
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
    }

    private void insertData() {
        if(mPerson.mEmail == null || mPerson.mEmail.isEmpty()){
            mPersonDetailsView.mEmail.setVisibility(View.GONE);
            mPersonDetailsView.mEmailLayout.setVisibility(View.GONE);
        } else {
            mPersonDetailsView.mEmail.setText(mPerson.mEmail);
        }

        if(mPerson.mFullName == null || mPerson.mFullName.isEmpty()){

        } else {
            mPersonDetailsView.mPersonProfileName.setText(mPerson.mFullName);
        }

        if (mPerson.mContact.get("phone") == null || mPerson.mContact.get("phone").isEmpty()){
            mPersonDetailsView.mEmailPhoneSeparator.setVisibility(View.GONE);
            mPersonDetailsView.mMobileNumber.setVisibility(View.GONE);
            mPersonDetailsView.mPhoneLayout.setVisibility(View.GONE);
        } else {
            mPersonDetailsView.mMobileNumber.setText(mPerson.mContact.get("phone"));
        }

        if (mPerson.mContact.get("homePhone") == null || mPerson.mContact.get("homePhone").isEmpty()){
            mPersonDetailsView.mMobileHomeSeparator.setVisibility(View.GONE);
            mPersonDetailsView.mHomePhone.setVisibility(View.GONE);
            mPersonDetailsView.mHomePhoneLayout.setVisibility(View.GONE);
        } else {
            mPersonDetailsView.mHomePhone.setText(mPerson.mContact.get("homePhone"));
        }

        if (mPerson.mContact.get("workPhone") == null || mPerson.mContact.get("workPhone").isEmpty()){
            mPersonDetailsView.mHomeWorkSeparator.setVisibility(View.GONE);
            mPersonDetailsView.mWorkPhone.setVisibility(View.GONE);
            mPersonDetailsView.mWorkPhoneLayout.setVisibility(View.GONE);
        } else {
            mPersonDetailsView.mWorkPhone.setText(mPerson.mContact.get("workPhone"));
        }

        if (mPerson.mContact.get("street") == null || mPerson.mContact.get("street").isEmpty()){
            mPersonDetailsView.mGenderAddressSeparator.setVisibility(View.GONE);
            mPersonDetailsView.mAddress.setVisibility(View.GONE);
            mPersonDetailsView.mAddressLayout.setVisibility(View.GONE);
        } else {
            mPersonDetailsView.mAddress.setText(mPerson.mContact.get("street"));
        }

        if (mPerson.mContact.get("city") == null || mPerson.mContact.get("city").isEmpty()){
            mPersonDetailsView.mAddressCitySeparator.setVisibility(View.GONE);
            mPersonDetailsView.mCity.setVisibility(View.GONE);
            mPersonDetailsView.mCityLayout.setVisibility(View.GONE);
        } else {
            mPersonDetailsView.mCity.setText(mPerson.mContact.get("city"));
        }

        if (mPerson.mContact.get("zipcode") == null || mPerson.mContact.get("zipcode").isEmpty()){
            mPersonDetailsView.mCityPostalSeparator.setVisibility(View.GONE);
            mPersonDetailsView.mPostalCode.setVisibility(View.GONE);
            mPersonDetailsView.mPostalCodeLayout.setVisibility(View.GONE);
        } else {
            mPersonDetailsView.mPostalCode.setText(mPerson.mContact.get("zipcode"));
        }

        if (mPerson.mOccupation == null || mPerson.mOccupation.isEmpty()){
            mPersonDetailsView.mWorkJobSeparator.setVisibility(View.GONE);
            mPersonDetailsView.mJobTitle.setVisibility(View.GONE);
            mPersonDetailsView.mJobLayout.setVisibility(View.GONE);
        } else {
            mPersonDetailsView.mJobTitle.setText(mPerson.mOccupation);
        }

        if (mPerson.mGender == null || mPerson.mGender.isEmpty()){
            mPersonDetailsView.mRegisteredGenderSeparator.setVisibility(View.GONE);
            mPersonDetailsView.mGender.setVisibility(View.GONE);
            mPersonDetailsView.mGenderLayout.setVisibility(View.GONE);
        } else {
            if (mPerson.mGender.equals("male"))
                mPersonDetailsView.mGender.setText(mContext.getString(R.string.gender_male));
            if (mPerson.mGender.equals("female"))
                mPersonDetailsView.mGender.setText(mContext.getString(R.string.gender_female));
        }

        if (mPerson.mTags == null || mPerson.mTags.size() == 0){
            mPersonDetailsView.mTagsView.setVisibility(View.GONE);
            mPersonDetailsView.mTagsLayout.setVisibility(View.GONE);
        } else {
            mPersonDetailsView.mTagsView.setText(String.valueOf(mPerson.mTags.size()));
        }

        if(mPerson.mPictureUrl.get("url") != null) {
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

    private void insertTimeString(){
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

        if (mPerson.mBirthday == null){
            mPersonDetailsView.mJobBirthSeparator.setVisibility(View.GONE);
            mPersonDetailsView.mBirthday.setVisibility(View.GONE);
            mPersonDetailsView.mBirthdayLayout.setVisibility(View.GONE);
        } else {
            String birthdayString;
            Calendar birthday = Calendar.getInstance();
            birthday.setTimeInMillis(mPerson.mBirthday.getTime() + tz.getOffset(mPerson.mBirthday.getTime()));
            birthdayString = CalendarUtils.checkNumber(birthday.get(Calendar.DATE)) + " "
                    + months[birthday.get(Calendar.MONTH)].substring(0, 3) + " " + birthday.get(Calendar.YEAR);
            mPersonDetailsView.mBirthday.setText(birthdayString);
        }
    }

    private LinearLayout.OnClickListener mTagsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new TagsListAdapter(), R.string.person_select_tags);
            dialog.showCancelButton(false);
            dialog.setOnItemClickListener(null);
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

