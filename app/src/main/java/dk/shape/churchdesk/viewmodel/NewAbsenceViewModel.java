package dk.shape.churchdesk.viewmodel;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import com.roomorama.caldroid.CaldroidListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Category;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.entity.resources.Resource;
import dk.shape.churchdesk.request.CreateEventRequest;
import dk.shape.churchdesk.util.CalendarUtils;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.MultiSelectDialog;
import dk.shape.churchdesk.view.MultiSelectListItemView;
import dk.shape.churchdesk.view.NewAbsenceView;
import dk.shape.churchdesk.view.SingleSelectDialog;
import dk.shape.churchdesk.view.SingleSelectListItemView;
import dk.shape.churchdesk.view.TimePickerDialog;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by chirag on 23/02/16.
 */
public class NewAbsenceViewModel  extends ViewModel<NewAbsenceView> {


    private Context mContext;
    private NewAbsenceView mNewAbsenceView;

    private final SendOkayListener mSendOkayListener;

    private final User mCurrentUser;
    private List<Group> mGroups;
    private List<Category> mCategories;
    private List<OtherUser> mOtherUsers;

    private static Site mSelectedSite;
    private static boolean msendNotifications;
    private static Group mSelectedGroup;
    private static List<Integer> mSelectedCategories = new ArrayList<>();
    private static List<Integer> mSelectedResources = new ArrayList<>();
    private static List<Integer> mSelectedOtherUsers = new ArrayList<>();

    //timestart
    Calendar calStart = Calendar.getInstance();
    //timeEnd
    Calendar calEnd = Calendar.getInstance();

    public NewAbsenceViewModel(User mCurrentUser, SendOkayListener listener) {
        this.mCurrentUser = mCurrentUser;
        this.mSendOkayListener = listener;

    }

    public interface SendOkayListener {
        void okay(boolean isOkay, CreateEventRequest.EventParameter parameter);
    }

    @Override
    public void bind(NewAbsenceView newAbsenceView) {
        mContext = newAbsenceView.getContext();
        mNewAbsenceView = newAbsenceView;

        setDefaultText();

        mNewAbsenceView.mTimeAlldayChosen.setOnCheckedChangeListener(mAllDaySwitchListener);
        mNewAbsenceView.mTimeStart.setOnClickListener(mStartTimeClickListener);
        mNewAbsenceView.mTimeEnd.setOnClickListener(mEndTimeClickListener);
        mNewAbsenceView.mSiteParish.setOnClickListener(mSiteParishClickListener);
        mNewAbsenceView.mSiteGroup.setOnClickListener(mGroupClickListener);
        mNewAbsenceView.mSiteCategory.setOnClickListener(mCategoryClickListener);
        mNewAbsenceView.mUsers.setOnClickListener(mUsersClickListener);

        mNewAbsenceView.mSubstituteChosen.addTextChangedListener(mValidateTextWatcher);
        mNewAbsenceView.mCommentsChosen.addTextChangedListener(mValidateTextWatcher);

    }

    public void setDataToEdit(Event event){
        mSelectedSite = mCurrentUser.getSiteByUrl(event.mSiteUrl);
        validateNewSiteParish(mSelectedSite);
        //mNewAbsenceView.mTitleChosen.setText(event.mTitle);
        mNewAbsenceView.mTimeAlldayChosen.setChecked(event.isAllDay);
        TimeZone tz = TimeZone.getDefault();
        // Make sure that we are dealing with the timezones.
        calStart.setTimeInMillis(event.mStartDate.getTime() + tz.getOffset(event.mStartDate.getTime()));
        calEnd.setTimeInMillis(event.mEndDate.getTime() + tz.getOffset(event.mEndDate.getTime()));

        setTime(event.isAllDay);
        mNewAbsenceView.mSiteParish.setVisibility(View.GONE);
        mNewAbsenceView.mParishGroupSeperator.setVisibility(View.GONE);
        mSelectedGroup = DatabaseUtils.getInstance().getGroupById(event.getGroupId());
        mNewAbsenceView.mSiteGroupChosen.setText(mSelectedGroup.mName);

        if (mSelectedCategories == null) {
            mSelectedCategories = new ArrayList<>();
        }

        for (Integer catId: event.mCategories.keySet()) {
            mSelectedCategories.add(catId);
        }
        setCategoriesText();

        if (mSelectedOtherUsers == null) {
            mSelectedOtherUsers = new ArrayList<>();
        }

        for (Integer k : event.mUsers.keySet()) {
            mSelectedOtherUsers.add(k);
        }

        mOtherUsers = DatabaseUtils.getInstance().getOtherUsersBySite(event.mSiteUrl);
        setUsersText();

        mNewAbsenceView.mUsers.setVisibility(View.VISIBLE);

        mNewAbsenceView.mSubstituteChosen.setText(event.mSubstitute);
        mNewAbsenceView.mCommentsChosen.setText(event.mComments);

        validate();
    }

    TextWatcher mValidateTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validate();
        }
        @Override
        public void afterTextChanged(Editable s) {}
    };

    CompoundButton.OnCheckedChangeListener mValidateCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            validate();
        }
    };

    private void validate(){
        boolean isOkay = true;
        //String title = "" + mNewAbsenceView.mTitleChosen.getText().toString().trim();
        if(mSelectedSite == null ||
                calStart == null ||
                calEnd == null ||
                mSelectedCategories == null || mSelectedCategories.isEmpty() ||
                mSelectedOtherUsers == null || mSelectedOtherUsers.isEmpty() ||
                mSelectedGroup == null || mSelectedGroup.id.isEmpty() || mSelectedGroup.id.length() > 255
                ){
            isOkay = false;
        }

        //Lav request parameter
        if(isOkay) {
            if (mNewAbsenceView.mTimeAlldayChosen.isChecked()) {
                calEnd.set(Calendar.HOUR_OF_DAY, 23);
                calEnd.set(Calendar.MINUTE, 59);
                calEnd.set(Calendar.SECOND, 59);
            }
            CreateEventRequest.EventParameter mEventParameter = new CreateEventRequest.EventParameter(
                    "absence",
                    mSelectedSite.mSiteUrl,
                    mSelectedGroup.getId(),
                    "",
                    mNewAbsenceView.mTimeAlldayChosen.isChecked(),
                    msendNotifications,
                    false,
                    calEnd.getTime(),
                    calStart.getTime(),
                    "",
                    mSelectedResources,
                    mSelectedOtherUsers,
                    "",
                    "",
                    "",
                    mSelectedCategories,
                    "",
                    "",
                    mNewAbsenceView.mSubstituteChosen.getText().toString().trim(),
                    mNewAbsenceView.mCommentsChosen.getText().toString().trim());
            mSendOkayListener.okay(isOkay, mEventParameter);
        }
    }

    private void setDefaultText(){
        calEnd.add(Calendar.HOUR_OF_DAY, 1);
        mSelectedSite = mCurrentUser.mSites.get(0);
        validateNewSiteParish(mSelectedSite);
    }

    private void validateNewSiteParish(Site site){
        mSelectedSite = site;
        mSelectedGroup = null;
        mSelectedCategories = null;
        mSelectedOtherUsers = null;
        mGroups = DatabaseUtils.getInstance().getGroupsBySiteId(mSelectedSite.mSiteUrl, mCurrentUser);
        mCategories = DatabaseUtils.getInstance().getAbsenceCategoriesBySiteId(mSelectedSite.mSiteUrl);
        mOtherUsers = DatabaseUtils.getInstance().getOtherUsersBySite(mSelectedSite.mSiteUrl);;
        mNewAbsenceView.mUsers.setVisibility(View.VISIBLE);
        if(mGroups == null || mGroups.isEmpty()){
            mNewAbsenceView.mSiteGroupChosen.setText(R.string.new_event_none_available);
            mNewAbsenceView.mSiteGroupChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            //mNewAbsenceView.mUsers.setVisibility(View.GONE);
        }else if(mSelectedGroup == null){
            mNewAbsenceView.mSiteGroupChosen.setText("");
            mNewAbsenceView.mSiteGroupChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.disclosure_arrow), null);
            //mNewAbsenceView.mUsers.setVisibility(View.GONE);
        }
        if(mCategories == null || mCategories.isEmpty()){
            mNewAbsenceView.mSiteCategoryChosen.setText(R.string.new_event_none_available);
            mNewAbsenceView.mSiteCategoryChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            mNewAbsenceView.mSiteCategoryChosen.setText("");
            mNewAbsenceView.mSiteCategoryChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.disclosure_arrow), null);
        }
        mNewAbsenceView.mSiteParishChosen.setText(mSelectedSite.mSiteName);
    }

    private SwitchCompat.OnCheckedChangeListener mAllDaySwitchListener = new SwitchCompat.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!mNewAbsenceView.mTimeStartChosen.getText().toString().isEmpty()) {
                setTime(isChecked);
            }
        }
    };

    private View.OnClickListener mStartTimeClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //This should start the datepicker and send the state of the all-day switch

            FragmentTransaction ft = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
            final TimePickerDialog timePickerDialog = new TimePickerDialog();
            Bundle b = new Bundle();
            b.putLong("date", calStart.getTimeInMillis());
            b.putBoolean("allDay", mNewAbsenceView.mTimeAlldayChosen.isChecked());
            timePickerDialog.setArguments(b);
            timePickerDialog.setOnSelectDateListener(new CaldroidListener() {
                @Override
                public void onSelectDate(Calendar date, View view) {
                    mNewAbsenceView.mTimeEnd.setVisibility(View.VISIBLE);
                    if (timePickerDialog.caldroidFragment.isDateSelected(timePickerDialog.convertDateToDateTime(date))) {
                        timePickerDialog.caldroidFragment.deselectDate(date);
                        calStart.setTimeInMillis(System.currentTimeMillis());
                        calEnd.setTimeInMillis(System.currentTimeMillis());
                        calEnd.add(Calendar.HOUR_OF_DAY, 1);
                        setTime(mNewAbsenceView.mTimeAlldayChosen.isChecked());
                    } else {
                        Calendar now = Calendar.getInstance();
                        now.add(Calendar.DATE, -1);
                        if (now.before(date)) {
                            timePickerDialog.caldroidFragment.clearSelectedDates();
                            timePickerDialog.caldroidFragment.selectDate(date);
                            //Select date
                            calStart.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
                            calEnd.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
                            setTime(mNewAbsenceView.mTimeAlldayChosen.isChecked());
                            if(!mNewAbsenceView.mTimeAlldayChosen.isChecked()){
                                timePickerDialog.mButtonSwitch.clickOnButton(1);
                            }
                        }
                    }
                }
            });

            timePickerDialog.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    calStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calStart.set(Calendar.MINUTE, minute);
                    calEnd.setTime(calStart.getTime());
                    calEnd.add(Calendar.HOUR_OF_DAY, 1);
                    setTime(mNewAbsenceView.mTimeAlldayChosen.isChecked());
                }
            });

            timePickerDialog.show(ft, "dialog");
        }
    };

    private void setTime(boolean isChecked){
        String[] months = mContext.getResources().getStringArray(R.array.months);
        String dateStart = calStart.get(Calendar.DATE) + " " + months[calStart.get(Calendar.MONTH)] +" " + calStart.get(Calendar.YEAR);
        String dateEnd = calEnd.get(Calendar.DATE) + " " + months[calEnd.get(Calendar.MONTH)] +" " + calEnd.get(Calendar.YEAR);
        if(isChecked){
            mNewAbsenceView.mTimeStartChosen.setText(dateStart);
            mNewAbsenceView.mTimeEndChosen.setText(dateEnd);
        } else {
            String timeStart = CalendarUtils.translateTime(calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE));
            String timeEnd = CalendarUtils.translateTime(calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE));
            mNewAbsenceView.mTimeStartChosen.setText(dateStart + "   " + timeStart);
            mNewAbsenceView.mTimeEndChosen.setText(dateEnd + "   " + timeEnd);
        }
        validate();
    }

    private View.OnClickListener mEndTimeClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            FragmentTransaction ft = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
            final TimePickerDialog timePickerDialog = new TimePickerDialog();
            Bundle b = new Bundle();
            b.putBoolean("allDay", mNewAbsenceView.mTimeAlldayChosen.isChecked());
            b.putLong("date", calEnd.getTimeInMillis());
            timePickerDialog.setArguments(b);
            timePickerDialog.setOnSelectDateListener(new CaldroidListener() {
                @Override
                public void onSelectDate(Calendar date, View view) {
                    if (timePickerDialog.caldroidFragment.isDateSelected(timePickerDialog.convertDateToDateTime(date))) {
                        timePickerDialog.caldroidFragment.deselectDate(date);
                        calEnd.setTime(calStart.getTime());
                        calEnd.add(Calendar.HOUR_OF_DAY, 1);
                        setTime(mNewAbsenceView.mTimeAlldayChosen.isChecked());
                    } else {
                        date.set(Calendar.HOUR_OF_DAY, calEnd.get(Calendar.HOUR_OF_DAY));
                        date.set(Calendar.MINUTE, calEnd.get(Calendar.MINUTE));
                        if (calStart.before(date)) {
                            timePickerDialog.caldroidFragment.clearSelectedDates();
                            timePickerDialog.caldroidFragment.selectDate(date);
                            //Select date
                            calEnd.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
                            setTime(mNewAbsenceView.mTimeAlldayChosen.isChecked());
                        }
                    }
                }
            });
            timePickerDialog.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    calEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calEnd.set(Calendar.MINUTE, minute);
                    if (calStart.getTimeInMillis() < calEnd.getTimeInMillis()) {
                    } else {
                        calEnd.add(Calendar.DATE, 1);
                    }
                    setTime(mNewAbsenceView.mTimeAlldayChosen.isChecked());
                }
            });
            timePickerDialog.show(ft, "dialog");
        }
    };

    private View.OnClickListener mSiteParishClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //this should let you choose a parish
            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new SiteListAdapter(), R.string.new_event_parish_chooser);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    validateNewSiteParish(mCurrentUser.mSites.get(position));
                    mNewAbsenceView.mUsers.setVisibility(View.VISIBLE);
                    mOtherUsers = DatabaseUtils.getInstance().getOtherUsersBySite(mSelectedSite.mSiteUrl);
                    if(mSelectedOtherUsers != null){
                        mSelectedOtherUsers.clear();
                    }
                    mNewAbsenceView.mUsersChosen.setText("");
                }
            });
            dialog.show();
        }
    };

    private View.OnClickListener mGroupClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //This should let you choose a group
            mGroups = DatabaseUtils.getInstance().getGroupsBySiteId(mSelectedSite.mSiteUrl, mCurrentUser);

            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new GroupListAdapter(), R.string.new_event_group_chooser);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    mSelectedGroup = mGroups.get(position);
                    mNewAbsenceView.mSiteGroupChosen.setText(mSelectedGroup.mName);
                    validate();
                }
            });
            dialog.show();
        }
    };

    private View.OnClickListener mCategoryClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //This should let you choose a category

            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new CategoryListAdapter(), R.string.new_event_category_chooser);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(mSelectedCategories == null){
                        mSelectedCategories = new ArrayList<>();
                    }
                    if (mSelectedCategories.size() == 0)
                        mSelectedCategories.add(mCategories.get(position).getId());
                    else
                        mSelectedCategories.set(0, mCategories.get(position).getId());
                    setCategoriesText();
                    dialog.dismiss();

                    validate();
                }
            });
            dialog.show();

        }
    };

    private void setCategoriesText(){
        if(mSelectedCategories.size() > 1){
            mNewAbsenceView.mSiteCategoryChosen.setText(String.valueOf(mSelectedCategories.size()));
        } else if (mSelectedCategories.size() == 1){
            String mCategoryName = "";
            for(Category category : mCategories){
                if(category.getId() == mSelectedCategories.get(0)){
                    mCategoryName = category.mName;
                }
            }
            mNewAbsenceView.mSiteCategoryChosen.setText(mCategoryName);
        } else {
            mNewAbsenceView.mSiteCategoryChosen.setText("");
        }
    }

    private View.OnClickListener mUsersClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //This should let you choose the used resources

            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new UserListAdapter(), R.string.new_event_users_chooser);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(mSelectedOtherUsers == null){
                        mSelectedOtherUsers = new ArrayList<>();
                    }
                    if (mSelectedOtherUsers.size() == 0)
                        mSelectedOtherUsers.add(mOtherUsers.get(position).getId());
                    else
                        mSelectedOtherUsers.set(0, mOtherUsers.get(position).getId());
                    setUsersText();
                    dialog.dismiss();
                    validate();
                }
            });
            dialog.show();
        }
    };

    private void setUsersText(){
        if(mSelectedOtherUsers.size() > 1){
            mNewAbsenceView.mUsersChosen.setText(String.valueOf(mSelectedOtherUsers.size()));
        } else if (mSelectedOtherUsers.size() == 1){
            String oUserName = "";
            for(OtherUser oUser : mOtherUsers){
                if(mSelectedOtherUsers.get(0) == oUser.getId()){
                    oUserName = oUser.mName;
                }
            }
            mNewAbsenceView.mUsersChosen.setText(oUserName);
        } else {
            mNewAbsenceView.mUsersChosen.setText("");
        }
    }

    private class SiteListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCurrentUser.mSites.size();
        }

        @Override
        public Object getItem(int position) {
            return mCurrentUser.mSites.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Site site = mCurrentUser.mSites.get(position);

            SingleSelectListItemView view = new SingleSelectListItemView(mContext);
            view.mItemTitle.setText(site.mSiteName);
            view.mItemSelected.setVisibility(
                    mSelectedSite != null && site.equals(mSelectedSite.mSiteUrl)
                            ? View.VISIBLE
                            : View.GONE);
            return view;
        }
    }

    private class GroupListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mGroups != null ? mGroups.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mGroups.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Group group = mGroups.get(position);

            SingleSelectListItemView view = new SingleSelectListItemView(mContext);
            view.mItemTitle.setText(group.mName);
            view.mItemSelected.setVisibility(
                    mSelectedGroup != null && group.equals(mSelectedGroup)
                            ? View.VISIBLE
                            : View.GONE);
            return view;
        }
    }

    private class CategoryListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCategories != null ? mCategories.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Category category = mCategories.get(position);

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            view.mItemTitle.setText(category.mName);
            view.mItemSelected.setVisibility(
                    mSelectedCategories != null && mSelectedCategories.contains(category.getId())
                            ? View.VISIBLE
                            : View.GONE);
            view.mItemDot.setTextColor(category.getColor());

            return view;
        }
    }

    private class UserListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mOtherUsers != null ? mOtherUsers.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mOtherUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OtherUser user = mOtherUsers.get(position);

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            view.mItemTitle.setText(user.mName);
            view.mItemSelected.setVisibility(
                    mSelectedOtherUsers != null && mSelectedOtherUsers.contains(user.getId())
                            ? View.VISIBLE
                            : View.GONE);
            view.mItemDot.setVisibility(View.GONE);
            view.mItemImage.setVisibility(View.VISIBLE);
            if(user.mPictureUrl != null && !user.mPictureUrl.isEmpty()) {
                Picasso.with(mContext)
                        .load(user.mPictureUrl)
                        .into(view.mItemImage);
            } else {
                //view.mItemImage.setImageResource(R.color.default_background);
                Picasso.with(mContext)
                        .load(R.drawable.user_default)
                        .into(view.mItemImage);
            }

            return view;
        }
    }
}
