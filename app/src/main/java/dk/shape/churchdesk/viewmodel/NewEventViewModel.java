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
import dk.shape.churchdesk.view.NewEventView;
import dk.shape.churchdesk.view.SingleSelectDialog;
import dk.shape.churchdesk.view.SingleSelectListItemView;
import dk.shape.churchdesk.view.TimePickerDialog;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by Martin on 20/05/2015.
 */
public class NewEventViewModel extends ViewModel<NewEventView> {


    private Context mContext;
    private NewEventView mNewEventView;

    private final SendOkayListener mSendOkayListener;

    private final User mCurrentUser;
    private List<Group> mGroups;
    private List<String> mVisibilityChoices;
    private List<Category> mCategories;
    private List<Resource> mResources;
    private List<OtherUser> mOtherUsers;

    private static Site mSelectedSite;
    private static boolean msendNotifications;
    private static Group mSelectedGroup;
    private static List<Integer> mSelectedCategories = new ArrayList<>();
    private static List<Integer> mSelectedResources = new ArrayList<>();
    private static List<Integer> mSelectedOtherUsers = new ArrayList<>();
    private static String mSelectedVisibility;

    //timestart
    Calendar calStart = Calendar.getInstance();
    //timeEnd
    Calendar calEnd = Calendar.getInstance();

    public NewEventViewModel(User mCurrentUser, SendOkayListener listener) {
        this.mCurrentUser = mCurrentUser;
        this.mVisibilityChoices = new ArrayList<>();
        this.mSendOkayListener = listener;

    }

    public interface SendOkayListener {
        void okay(boolean isOkay, CreateEventRequest.EventParameter parameter);
    }

    @Override
    public void bind(NewEventView newEventView) {
        mContext = newEventView.getContext();
        mNewEventView = newEventView;

        this.mVisibilityChoices.add(mContext.getString(R.string.event_details_visibility_website));
        this.mVisibilityChoices.add(mContext.getString(R.string.event_details_visibility_group));
        mSelectedVisibility = mVisibilityChoices.get(1);

        setDefaultText();

        mNewEventView.mTimeAlldayChosen.setOnCheckedChangeListener(mAllDaySwitchListener);
        mNewEventView.mTimeStart.setOnClickListener(mStartTimeClickListener);
        mNewEventView.mTimeEnd.setOnClickListener(mEndTimeClickListener);
        mNewEventView.mSiteParish.setOnClickListener(mSiteParishClickListener);
        mNewEventView.mSiteGroup.setOnClickListener(mGroupClickListener);
        mNewEventView.mSiteCategory.setOnClickListener(mCategoryClickListener);
        mNewEventView.mResources.setOnClickListener(mResourcesClickListener);
        mNewEventView.mVisibility.setOnClickListener(mVisibilityClickListener);
        mNewEventView.mUsers.setOnClickListener(mUsersClickListener);

        mNewEventView.mTitleChosen.addTextChangedListener(mValidateTextWatcher);
        mNewEventView.mPriceChosen.addTextChangedListener(mValidateTextWatcher);
        mNewEventView.mLocationChosen.addTextChangedListener(mValidateTextWatcher);
        mNewEventView.mContributorChosen.addTextChangedListener(mValidateTextWatcher);
        mNewEventView.mNoteChosen.addTextChangedListener(mValidateTextWatcher);
        mNewEventView.mDescriptionChosen.addTextChangedListener(mValidateTextWatcher);

        mNewEventView.mAllowDoubleBookingChosen.setOnCheckedChangeListener(mValidateCheckedChangedListener);

    }

    public void setDataToEdit(Event event){
        mSelectedSite = mCurrentUser.getSiteByUrl(event.mSiteUrl);
        validateNewSiteParish(mSelectedSite);
        mNewEventView.mTitleChosen.setText(event.mTitle);
        mNewEventView.mTimeAlldayChosen.setChecked(event.isAllDay);

        // Make sure that we are dealing with the timezones.
        calStart.setTimeInMillis(event.mStartDate.getTime() + calStart.getTimeZone().getRawOffset());
        calEnd.setTimeInMillis(event.mEndDate.getTime() + calEnd.getTimeZone().getRawOffset());

        setTime(event.isAllDay);
        mNewEventView.mSiteParish.setVisibility(View.GONE);
        mNewEventView.mParishGroupSeperator.setVisibility(View.GONE);
        mSelectedGroup = DatabaseUtils.getInstance().getGroupById(event.getGroupId());
        mNewEventView.mSiteGroupChosen.setText(mSelectedGroup.mName);

        if (mSelectedCategories == null) {
            mSelectedCategories = new ArrayList<>();
        }

        for (Integer catId: event.mCategories.keySet()) {
            mSelectedCategories.add(catId);
        }
        setCategoriesText();

        mNewEventView.mLocationChosen.setText(event.mLocation);

        if (mSelectedOtherUsers == null) {
            mSelectedOtherUsers = new ArrayList<>();
        }

        for (Integer k : event.mUsers.keySet()) {
            mSelectedOtherUsers.add(k);
        }

        mOtherUsers = DatabaseUtils.getInstance().getOtherUsersByGroupAndSite(Integer.valueOf(mSelectedGroup.id), event.mSiteUrl);
        setUsersText();

        mNewEventView.mUsers.setVisibility(View.VISIBLE);

        if (mSelectedResources == null) {
            mSelectedResources = new ArrayList<>();
        }

        for (Integer k : event.mResources.keySet()) {
            mSelectedResources.add(k);
        }

        if(mResources == null || mResources.isEmpty()){
            mNewEventView.mResourcesChosen.setText(R.string.new_event_none_available);
            mNewEventView.mResourcesChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            setResText();
        }
        mNewEventView.mNoteChosen.setText(event.mInternalNote);
        mNewEventView.mDescriptionChosen.setText(event.mDescription);
        mNewEventView.mContributorChosen.setText(event.mContributor);
        mNewEventView.mPriceChosen.setText(event.mPrice);
        if (event.mVisibility.equals("web"))
            mNewEventView.mVisibilityChosen.setText(mVisibilityChoices.get(0));
        else
            mNewEventView.mVisibilityChosen.setText(mVisibilityChoices.get(1));
        mNewEventView.mTimeEnd.setVisibility(View.VISIBLE);

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
        String title = "" + mNewEventView.mTitleChosen.getText().toString().trim();
        String price = mNewEventView.mPriceChosen.getText().toString().trim();
        if(mSelectedSite == null ||
                title.isEmpty()|| title.length() > 255 ||
                calStart == null ||
                calEnd == null ||
                mSelectedCategories == null || mSelectedCategories.isEmpty() ||
                mSelectedGroup == null || mSelectedGroup.id.isEmpty() || mSelectedGroup.id.length() > 255 ||
                mNewEventView.mLocationChosen.getText().toString().trim().length() > 255 ||
                mNewEventView.mContributorChosen.getText().toString().trim().length() > 255 ||
                mNewEventView.mPriceChosen.getText().toString().trim().length() > 250
                ){
            isOkay = false;
        }

        //Lav request parameter
        if(isOkay) {
            CreateEventRequest.EventParameter mEventParameter = new CreateEventRequest.EventParameter(
                    mSelectedSite.mSiteUrl,
                    mSelectedGroup.getId(),
                    title,
                    mNewEventView.mTimeAlldayChosen.isChecked(),
                    msendNotifications,
                    mNewEventView.mAllowDoubleBookingChosen.isChecked(),
                    calEnd.getTime(),
                    calStart.getTime(),
                    mSelectedVisibility.equals(mContext.getString(R.string.event_details_visibility_website)) ? "web" : "group",
                    mSelectedResources,
                    mSelectedOtherUsers,
                    mNewEventView.mLocationChosen.getText().toString().trim(),
                    price,
                    mNewEventView.mContributorChosen.getText().toString().trim(),
                    mSelectedCategories,
                    mNewEventView.mNoteChosen.getText().toString().trim(),
                    mNewEventView.mDescriptionChosen.getText().toString().trim());
            mSendOkayListener.okay(isOkay, mEventParameter);
        }
    }

    private void setDefaultText(){
        calEnd.add(Calendar.HOUR_OF_DAY, 1);
        mSelectedSite = mCurrentUser.mSites.get(0);
        validateNewSiteParish(mSelectedSite);
        mNewEventView.mVisibilityChosen.setText(R.string.event_details_visibility_group);
    }

    private void validateNewSiteParish(Site site){
        mSelectedSite = site;
        mSelectedGroup = null;
        mSelectedCategories = null;
        mSelectedResources = null;
        mSelectedOtherUsers = null;
        mGroups = DatabaseUtils.getInstance().getGroupsBySiteId(mSelectedSite.mSiteUrl, mCurrentUser);
        mCategories = DatabaseUtils.getInstance().getCategoriesBySiteId(mSelectedSite.mSiteUrl);
        mResources = DatabaseUtils.getInstance().getResourcesBySiteId(mSelectedSite.mSiteUrl);
        mOtherUsers = null;

        if(mGroups == null || mGroups.isEmpty()){
            mNewEventView.mSiteGroupChosen.setText(R.string.new_event_none_available);
            mNewEventView.mSiteGroupChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            mNewEventView.mUsers.setVisibility(View.GONE);
        }else if(mSelectedGroup == null){
            mNewEventView.mSiteGroupChosen.setText("");
            mNewEventView.mSiteGroupChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.disclosure_arrow), null);
            mNewEventView.mUsers.setVisibility(View.GONE);
        }
        if(mCategories == null || mCategories.isEmpty()){
            mNewEventView.mSiteCategoryChosen.setText(R.string.new_event_none_available);
            mNewEventView.mSiteCategoryChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            mNewEventView.mSiteCategoryChosen.setText("");
            mNewEventView.mSiteCategoryChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.disclosure_arrow), null);
        }
        if(mResources == null || mResources.isEmpty()){
            mNewEventView.mResourcesChosen.setText(R.string.new_event_none_available);
            mNewEventView.mResourcesChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            mNewEventView.mResourcesChosen.setText("");
            mNewEventView.mResourcesChosen.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.disclosure_arrow), null);
        }
        mNewEventView.mAllowDoubleBooking.setVisibility(mSelectedSite.mPermissions.get("canDoubleBook") ? View.VISIBLE : View.INVISIBLE);
        mNewEventView.mSiteParishChosen.setText(mSelectedSite.mSiteName);
    }

    private SwitchCompat.OnCheckedChangeListener mAllDaySwitchListener = new SwitchCompat.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!mNewEventView.mTimeStartChosen.getText().toString().isEmpty()) {
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
            b.putBoolean("allDay", mNewEventView.mTimeAlldayChosen.isChecked());
            timePickerDialog.setArguments(b);
            timePickerDialog.setOnSelectDateListener(new CaldroidListener() {
                @Override
                public void onSelectDate(Calendar date, View view) {
                    mNewEventView.mTimeEnd.setVisibility(View.VISIBLE);
                    if (timePickerDialog.caldroidFragment.isDateSelected(timePickerDialog.convertDateToDateTime(date))) {
                        timePickerDialog.caldroidFragment.deselectDate(date);
                        calStart.setTimeInMillis(System.currentTimeMillis());
                        calEnd.setTimeInMillis(System.currentTimeMillis());
                        calEnd.add(Calendar.HOUR_OF_DAY, 1);
                        setTime(mNewEventView.mTimeAlldayChosen.isChecked());
                    } else {
                        Calendar now = Calendar.getInstance();
                        now.add(Calendar.DATE, -1);
                        if (now.before(date)) {
                            timePickerDialog.caldroidFragment.clearSelectedDates();
                            timePickerDialog.caldroidFragment.selectDate(date);
                            //Select date
                            calStart.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
                            calEnd.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
                            setTime(mNewEventView.mTimeAlldayChosen.isChecked());
                            if(!mNewEventView.mTimeAlldayChosen.isChecked()){
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
                    setTime(mNewEventView.mTimeAlldayChosen.isChecked());
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
            mNewEventView.mTimeStartChosen.setText(dateStart);
            mNewEventView.mTimeEndChosen.setText(dateEnd);
        } else {
            String timeStart = CalendarUtils.translateTime(calStart.get(Calendar.HOUR_OF_DAY), calStart.get(Calendar.MINUTE));
            String timeEnd = CalendarUtils.translateTime(calEnd.get(Calendar.HOUR_OF_DAY), calEnd.get(Calendar.MINUTE));
            mNewEventView.mTimeStartChosen.setText(dateStart + "   " + timeStart);
            mNewEventView.mTimeEndChosen.setText(dateEnd + "   " + timeEnd);
        }
        validate();
    }

    private View.OnClickListener mEndTimeClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            FragmentTransaction ft = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
            final TimePickerDialog timePickerDialog = new TimePickerDialog();
            Bundle b = new Bundle();
            b.putBoolean("allDay", mNewEventView.mTimeAlldayChosen.isChecked());
            b.putLong("date", calEnd.getTimeInMillis());
            timePickerDialog.setArguments(b);
            timePickerDialog.setOnSelectDateListener(new CaldroidListener() {
                @Override
                public void onSelectDate(Calendar date, View view) {
                    if (timePickerDialog.caldroidFragment.isDateSelected(timePickerDialog.convertDateToDateTime(date))) {
                        timePickerDialog.caldroidFragment.deselectDate(date);
                        calEnd.setTime(calStart.getTime());
                        calEnd.add(Calendar.HOUR_OF_DAY, 1);
                        setTime(mNewEventView.mTimeAlldayChosen.isChecked());
                    } else {
                        date.set(Calendar.HOUR_OF_DAY, calEnd.get(Calendar.HOUR_OF_DAY));
                        date.set(Calendar.MINUTE, calEnd.get(Calendar.MINUTE));
                        if (calStart.before(date)) {
                            timePickerDialog.caldroidFragment.clearSelectedDates();
                            timePickerDialog.caldroidFragment.selectDate(date);
                            //Select date
                            calEnd.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
                            setTime(mNewEventView.mTimeAlldayChosen.isChecked());
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
                    setTime(mNewEventView.mTimeAlldayChosen.isChecked());
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
                    mNewEventView.mSiteGroupChosen.setText(mSelectedGroup.mName);
                    mNewEventView.mUsers.setVisibility(View.VISIBLE);
                    mOtherUsers = DatabaseUtils.getInstance().getOtherUsersByGroupAndSite(Integer.valueOf(mSelectedGroup.id), mSelectedSite.mSiteUrl);
                    if(mSelectedOtherUsers != null){
                        mSelectedOtherUsers.clear();
                        mNewEventView.mUsersChosen.setText("");
                    }
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

            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new CategoryListAdapter(), R.string.new_event_category_chooser);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(mSelectedCategories == null){
                        mSelectedCategories = new ArrayList<>();
                    }
                    if(mSelectedCategories.contains(mCategories.get(position).getId())){
                        mSelectedCategories.remove((Integer) mCategories.get(position).getId());
                    } else {
                        mSelectedCategories.add(mCategories.get(position).getId());
                    }
                    setCategoriesText();

                    ((MultiSelectListItemView)view).mItemSelected.setVisibility(
                            mSelectedCategories != null && mSelectedCategories.contains(mCategories.get(position).getId())
                                    ? View.VISIBLE
                                    : View.GONE);

                    validate();
                }
            });
            dialog.showCancelButton(false);
            dialog.setOnOKClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    };

    private void setCategoriesText(){
        if(mSelectedCategories.size() > 1){
            mNewEventView.mSiteCategoryChosen.setText(String.valueOf(mSelectedCategories.size()));
        } else if (mSelectedCategories.size() == 1){
            String mCategoryName = "";
            for(Category category : mCategories){
                if(category.getId() == mSelectedCategories.get(0)){
                    mCategoryName = category.mName;
                }
            }
            mNewEventView.mSiteCategoryChosen.setText(mCategoryName);
        } else {
            mNewEventView.mSiteCategoryChosen.setText("");
        }
    }

    private View.OnClickListener mResourcesClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //This should let you choose the used resources
            if(!mResources.isEmpty()) {
                final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                        new ResourceListAdapter(), R.string.new_event_resources_chooser);
                dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mSelectedResources == null) {
                            mSelectedResources = new ArrayList<>();
                        }
                        if (mSelectedResources.contains(mResources.get(position).getId())) {
                            mSelectedResources.remove((Integer) mResources.get(position).getId());
                        } else {
                            mSelectedResources.add(mResources.get(position).getId());
                        }
                        setResText();
                        ((MultiSelectListItemView) view).mItemSelected.setVisibility(
                                mSelectedResources != null && mSelectedResources.contains(mResources.get(position).getId())
                                        ? View.VISIBLE
                                        : View.GONE);
                        validate();
                    }
                });
                dialog.showCancelButton(false);
                dialog.setOnOKClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        }
    };

    private void setResText(){
        if (mSelectedResources.size() > 1) {
            mNewEventView.mResourcesChosen.setText(String.valueOf(mSelectedResources.size()));
        } else if (mSelectedResources.size() == 1) {
            String resourceName = "";
            for(Resource res : mResources){
                if(mSelectedResources.get(0) == res.getId()){
                    resourceName = res.mName;
                }
            }
            mNewEventView.mResourcesChosen.setText(resourceName);
        } else {
            mNewEventView.mResourcesChosen.setText("");
        }
    }

    private View.OnClickListener mUsersClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //This should let you choose the used resources

            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new UserListAdapter(), R.string.new_event_users_chooser);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(mSelectedOtherUsers == null){
                        mSelectedOtherUsers = new ArrayList<>();
                    }
                    if(mSelectedOtherUsers.contains(mOtherUsers.get(position).getId())){
                        mSelectedOtherUsers.remove((Integer) mOtherUsers.get(position).getId());
                    } else {
                        mSelectedOtherUsers.add(mOtherUsers.get(position).getId());
                    }
                    setUsersText();
                    ((MultiSelectListItemView)view).mItemSelected.setVisibility(
                            mSelectedOtherUsers != null && mSelectedOtherUsers.contains(mOtherUsers.get(position).getId())
                                    ? View.VISIBLE
                                    : View.GONE);
                    validate();
                }
            });
            dialog.showCancelButton(false);
            dialog.setOnOKClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    };

    private void setUsersText(){
        if(mSelectedOtherUsers.size() > 1){
            mNewEventView.mUsersChosen.setText(String.valueOf(mSelectedOtherUsers.size()));
        } else if (mSelectedOtherUsers.size() == 1){
            String oUserName = "";
            for(OtherUser oUser : mOtherUsers){
                if(mSelectedOtherUsers.get(0) == oUser.getId()){
                    oUserName = oUser.mName;
                }
            }
            mNewEventView.mUsersChosen.setText(oUserName);
        } else {
            mNewEventView.mUsersChosen.setText("");
        }
    }

    private View.OnClickListener mVisibilityClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //This should let you choose the visibility of the event

            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new VisibilityListAdapter(), R.string.new_event_visibility_chooser);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    mSelectedVisibility = mVisibilityChoices.get(position);
                    mNewEventView.mVisibilityChosen.setText(mSelectedVisibility);
                    validate();
                }
            });
            dialog.show();

        }
    };

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

    private class VisibilityListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mVisibilityChoices != null ? mVisibilityChoices.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mVisibilityChoices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String visibility = mVisibilityChoices.get(position);

            SingleSelectListItemView view = new SingleSelectListItemView(mContext);
            view.mItemTitle.setText(visibility);
            view.mItemSelected.setVisibility(
                    mSelectedVisibility != null && visibility.equals(mSelectedVisibility)
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

    private class ResourceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mResources != null ? mResources.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mResources.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Resource resource = mResources.get(position);

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            view.mItemTitle.setText(resource.mName);
            view.mItemSelected.setVisibility(
                    mSelectedResources != null && mSelectedResources.contains(resource.getId())
                            ? View.VISIBLE
                            : View.GONE);
            view.mItemDot.setTextColor(resource.getColor());

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