package dk.shape.churchdesk.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.AttendenceStatus;
import dk.shape.churchdesk.entity.Database;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Category;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.entity.resources.Resource;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.EventResponseRequest;
import dk.shape.churchdesk.util.CalendarUtils;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.AttendanceDialog;
import dk.shape.churchdesk.view.EventDetailsMultiItemView;
import dk.shape.churchdesk.view.EventDetailsView;
import dk.shape.churchdesk.view.MultiSelectDialog;
import dk.shape.churchdesk.view.MultiSelectListItemView;
import dk.shape.churchdesk.widget.CustomTextView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by Martin on 02/06/2015.
 */
public class EventDetailsViewModel extends ViewModel<EventDetailsView> {

    Event mEvent;
    User mUser;
    Site mSite;
    EventDetailsView mEventDetailsView;
    Context mContext;
    DatabaseUtils mDatabase;
    Event.Response mResponse;

    public EventDetailsViewModel(User mCurrentUser, Event event) {
        System.out.println(event.getId());

        this.mEvent = event;
        this.mUser = mCurrentUser;
        this.mSite = mUser.getSiteByUrl(mEvent.mSiteUrl);
    }

    @Override
    public void bind(EventDetailsView eventDetailsView) {
        mEventDetailsView = eventDetailsView;
        mContext = eventDetailsView.getContext();
        mDatabase = DatabaseUtils.getInstance();

        //Insert data
        insertData();

        //Set on click listeners
        mEventDetailsView.mLocationButton.setOnClickListener(mLocationButtonClickListener);
        mEventDetailsView.mCategoryLayout.setOnClickListener(mCategoriesClickListener);
        mEventDetailsView.mResourcesLayout.setOnClickListener(mResourcesClickListener);
        mEventDetailsView.mUsersLayout.setOnClickListener(mUsersClickListener);
        mEventDetailsView.mDescriptionButton.setOnClickListener(mDescriptionClickListener);
        mEventDetailsView.mNoteButton.setOnClickListener(mNoteClickListener);
        mEventDetailsView.mAttendanceButton.setOnClickListener(mAttendanceClickListener);

        //TODO: hvis der er et billede skal det findes her!
    }

    private void insertData() {
        mEventDetailsView.mTitle.setText(mEvent.mTitle);
        mEventDetailsView.mGroup.setText(mDatabase.getGroupById(mEvent.getGroupId()).mName);
        mEventDetailsView.mParish.setText(mUser.getSiteById(mEvent.mSiteUrl).mSiteName);

        //Time
        insertTimeString();

        //location
        if(mEvent.mLocation == null || mEvent.mLocation.isEmpty()){
            mEventDetailsView.mLocationLayout.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mLocation.setText(mEvent.mLocation);
        }

        //Categories
        mEventDetailsView.mCategoryView.removeAllViews();
        for(int i = 0; i < mEvent.mCategories.size(); i+=2){
            Drawable mColorDrawable1 = mContext.getResources().getDrawable(R.drawable.multiselect_circle);
            EventDetailsMultiItemView view = new EventDetailsMultiItemView(mContext);

            Category category1 = mDatabase.getCategoryById(mEvent.mCategories.get(i));
            view.mMultiCategory1.setText(category1.mName);
            if (mColorDrawable1 != null) {
                mColorDrawable1.setColorFilter(new PorterDuffColorFilter(category1.getColor(), PorterDuff.Mode.SRC));
            }
            view.mMultiCategory1.setCompoundDrawablesWithIntrinsicBounds(mColorDrawable1, null, null, null);

            if(i+1 < mEvent.mCategories.size()) {
                Drawable mColorDrawable2 = mContext.getResources().getDrawable(R.drawable.multiselect_circle);
                Category category2 = mDatabase.getCategoryById(mEvent.mCategories.get(i + 1));
                view.mMultiCategory2.setText(category2.mName);
                if (mColorDrawable2 != null) {
                    mColorDrawable2.setColorFilter(new PorterDuffColorFilter(category2.getColor(), PorterDuff.Mode.SRC));
                }
                view.mMultiCategory2.setCompoundDrawablesWithIntrinsicBounds(mColorDrawable2, null, null, null);
            }
            mEventDetailsView.mCategoryView.addView(view);
        }

        //Attendance
        boolean showAttendance = false;
        for(AttendenceStatus att : mEvent.mAttendenceStatus){
            if(att.getUser() == mSite.mUserId){
                mResponse = Event.Response.values()[att.getStatus()];
                setMyResponse();
                showAttendance = true;
            }
        }
        mEventDetailsView.mAttendanceButton.setVisibility(showAttendance ? View.VISIBLE : View.GONE);
        mEventDetailsView.mCategoryAttendanceSeperator.setVisibility(showAttendance ? View.VISIBLE : View.GONE);

        //Internal stuff
        boolean showInternalLayout = false;

        //Resources
        if(mEvent.mResources == null || mEvent.mResources.isEmpty()){
            mEventDetailsView.mResourcesLayout.setVisibility(View.GONE);
            mEventDetailsView.mResUsersSeperator.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mResourcesView.removeAllViews();
            for(int i = 0; i < mEvent.mResources.size(); i+=2){
                Drawable mColorDrawable1 = mContext.getResources().getDrawable(R.drawable.multiselect_circle);
                EventDetailsMultiItemView view = new EventDetailsMultiItemView(mContext);

                Resource res = mDatabase.getResourceById(mEvent.mResources.get(i));
                view.mMultiCategory1.setText(res.mName);
                if (mColorDrawable1 != null) {
                    mColorDrawable1.setColorFilter(new PorterDuffColorFilter(res.getColor(), PorterDuff.Mode.SRC));
                }
                view.mMultiCategory1.setCompoundDrawablesWithIntrinsicBounds(mColorDrawable1, null, null, null);

                if(i+1 < mEvent.mResources.size()) {
                    Drawable mColorDrawable2 = mContext.getResources().getDrawable(R.drawable.multiselect_circle);
                    Resource res2 = mDatabase.getResourceById(mEvent.mResources.get(i + 1));
                    view.mMultiCategory2.setText(res2.mName);
                    if (mColorDrawable2 != null) {
                        mColorDrawable2.setColorFilter(new PorterDuffColorFilter(res2.getColor(), PorterDuff.Mode.SRC));
                    }
                    view.mMultiCategory2.setCompoundDrawablesWithIntrinsicBounds(mColorDrawable2, null, null, null);
                }

                mEventDetailsView.mResourcesView.addView(view);
            }
            showInternalLayout = true;
        }

        //Users
        if(mEvent.mUsers == null || mEvent.mUsers.isEmpty()){
            mEventDetailsView.mUsersLayout.setVisibility(View.GONE);
            mEventDetailsView.mResUsersSeperator.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mUsersLayout.setVisibility(View.VISIBLE);
            mEventDetailsView.mUsersView.removeAllViews();
            for(int i = 0; i < mEvent.mUsers.size(); i+=2){
                EventDetailsMultiItemView view = new EventDetailsMultiItemView(mContext);
                OtherUser user1 = mDatabase.getUserById(mEvent.mUsers.get(i));
                view.mMultiCategory1.setText(user1.mName);
                view.mMultiCategory2.setCompoundDrawablePadding(0);
                if (i + 1 < mEvent.mUsers.size()) {
                    OtherUser user2 = mDatabase.getUserById(mEvent.mUsers.get(i + 1));
                    view.mMultiCategory2.setText(user2.mName);
                    view.mMultiCategory2.setCompoundDrawablePadding(0);
                }
                mEventDetailsView.mUsersView.addView(view);
            }
            showInternalLayout = true;
        }

        //Internal note
        if(mEvent.mInternalNote == null || mEvent.mInternalNote.isEmpty()){
            mEventDetailsView.mNoteButton.setVisibility(View.GONE);
            mEventDetailsView.mUsersNoteSeperator.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mNote.setText(mEvent.mInternalNote);
            showInternalLayout = true;
        }
        mEventDetailsView.mInternalLayout.setVisibility(showInternalLayout ? View.VISIBLE : View.GONE);

        //external stuff
        boolean showExternalLayout = false;

        //Contributor / Person
        if(mEvent.mPerson == null || mEvent.mPerson.isEmpty()){
            mEventDetailsView.mContributorLayout.setVisibility(View.GONE);
            mEventDetailsView.mContributorPriceSeperator.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mContributor.setText(mEvent.mPerson);
            showExternalLayout = true;
        }

        //Price
        if(mEvent.mPrice == null || mEvent.mPrice.isEmpty()){
            mEventDetailsView.mPriceLayout.setVisibility(View.GONE);
            mEventDetailsView.mPriceDescriptionSeperator.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mPrice.setText(mEvent.mPrice);
            showExternalLayout = true;
        }

        //Description
        if(mEvent.mDescription == null || mEvent.mDescription.isEmpty()){
            mEventDetailsView.mDescriptionButton.setVisibility(View.GONE);
            mEventDetailsView.mPriceDescriptionSeperator.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mDescription.setText(mEvent.mDescription);
            showExternalLayout = true;
        }
        mEventDetailsView.mExternalLayout.setVisibility(showExternalLayout ? View.VISIBLE : View.GONE);

        //Visibility of the event
        mEventDetailsView.mVisibility.setText(mEvent.mVisibility == 1 ? "Visible on website" : "Visible only in group");

        //Date the event is created
        Calendar createdCal = Calendar.getInstance();
        createdCal.setTime(mEvent.mCreatedAt);
        String[] months = mContext.getResources().getStringArray(R.array.months);
        mEventDetailsView.mDateCreated.setText(CalendarUtils.checkNumber(createdCal.get(Calendar.DATE)) + " "
                + months[createdCal.get(Calendar.MONTH)] + " "
                + createdCal.get(Calendar.YEAR));

        //Set it all visible
        mEventDetailsView.mLayout.setVisibility(View.VISIBLE);
    }

    private void setMyResponse(){
        switch (mResponse){
            case NO_ANSWER:
                mEventDetailsView.mAttendance.setText("No reply");
                mEventDetailsView.mAttendance.setTextColor(Color.BLACK);
                break;
            case YES:
                mEventDetailsView.mAttendance.setText("Going");
                mEventDetailsView.mAttendance.setTextColor(Color.GREEN);
                break;
            case MAYBE:
                mEventDetailsView.mAttendance.setText("Maybe");
                mEventDetailsView.mAttendance.setTextColor(Color.GRAY);
                break;
            case NO:
                mEventDetailsView.mAttendance.setText("Not going");
                mEventDetailsView.mAttendance.setTextColor(Color.RED);
                break;
            default:
                mEventDetailsView.mAttendance.setText("No reply");
                mEventDetailsView.mAttendance.setTextColor(Color.BLACK);
                break;
        }
    }

    private void insertTimeString(){
        //set time of event
        String timeOfEvent;
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(mEvent.mStartDate);
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(mEvent.mEndDate);
        String[] months = mContext.getResources().getStringArray(R.array.months);
        String[] weekdays = mContext.getResources().getStringArray(R.array.weekdays);
        if (!mEvent.isAllDay){
            if (startTime.get(Calendar.DATE) == endTime.get(Calendar.DATE) &&
                    startTime.get(Calendar.MONTH) == endTime.get(Calendar.MONTH) &&
                    startTime.get(Calendar.YEAR) == endTime.get(Calendar.YEAR)) {
                timeOfEvent = weekdays[startTime.get(Calendar.DAY_OF_WEEK)-1] + " "
                        + CalendarUtils.checkNumber(startTime.get(Calendar.DATE)) + " "
                        + months[startTime.get(Calendar.MONTH)].substring(0, 3) + " "
                        + CalendarUtils.translateTime(startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE)) + " - "
                        + CalendarUtils.translateTime(endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE));
            } else {
                timeOfEvent = weekdays[startTime.get(Calendar.DAY_OF_WEEK)-1].substring(0, 3) + " "
                        + CalendarUtils.checkNumber(startTime.get(Calendar.DATE)) + " "
                        + months[startTime.get(Calendar.MONTH)].substring(0, 3) + " "
                        + CalendarUtils.translateTime(startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE)) + " - "
                        + weekdays[endTime.get(Calendar.DAY_OF_WEEK)-1].substring(0, 3) + " "
                        + CalendarUtils.checkNumber(endTime.get(Calendar.DATE)) + " "
                        + months[endTime.get(Calendar.MONTH)].substring(0, 3) + " "
                        + CalendarUtils.translateTime(endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE));
            }
        } else {
            if (startTime.get(Calendar.DATE) == endTime.get(Calendar.DATE) &&
                    startTime.get(Calendar.MONTH) == endTime.get(Calendar.MONTH) &&
                    startTime.get(Calendar.YEAR) == endTime.get(Calendar.YEAR)) {
                timeOfEvent = weekdays[startTime.get(Calendar.DAY_OF_WEEK)-1] + " "
                        + CalendarUtils.checkNumber(startTime.get(Calendar.DATE)) + " "
                        + months[startTime.get(Calendar.MONTH)].substring(0, 3);
            } else {
                timeOfEvent = weekdays[startTime.get(Calendar.DAY_OF_WEEK)-1].substring(0, 3) + " "
                        + CalendarUtils.checkNumber(startTime.get(Calendar.DATE)) + " "
                        + months[startTime.get(Calendar.MONTH)].substring(0, 3) + " - "
                        + weekdays[endTime.get(Calendar.DAY_OF_WEEK)-1].substring(0, 3) + " "
                        + CalendarUtils.checkNumber(endTime.get(Calendar.DATE)) + " "
                        + months[endTime.get(Calendar.MONTH)].substring(0, 3);
            }
        }
        mEventDetailsView.mTime.setText(timeOfEvent);
    }

    private CustomTextView.OnClickListener mLocationButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Maps");
            builder.setMessage("Do you want to open Maps to get directions to '" + mEvent.mLocation + "'?");
            builder.setPositiveButton("Open Maps" , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + mEvent.mLocation);
                    Intent i = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    i.setPackage("com.google.android.apps.maps");
                    mContext.startActivity(i);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            Dialog d = builder.create();
            d.show();
        }
    };

    private LinearLayout.OnClickListener mCategoriesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new CategoryListAdapter(), R.string.event_details_categories_dialog);
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

    private LinearLayout.OnClickListener mResourcesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new ResourceListAdapter(), R.string.event_details_resource_dialog);
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

    private LinearLayout.OnClickListener mUsersClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new UsersListAdapter(), R.string.event_details_users_dialog);
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

    private LinearLayout.OnClickListener mDescriptionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    null, R.string.event_details_description_dialog);
            dialog.showOnlyText(mEvent.mDescription);
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

    private LinearLayout.OnClickListener mNoteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    null, R.string.event_details_note_dialog);
            dialog.showOnlyText(mEvent.mInternalNote);
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

    private LinearLayout.OnClickListener mAttendanceClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final AttendanceDialog dialog = new AttendanceDialog(mContext,
                    "Are you going to the event '" + mEvent.mTitle +"'?",
                    mEvent.getId(), mEvent.mSiteUrl);


            dialog.addOnClickListeners(
                    new CustomTextView.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            switch (v.getId()){
                                case R.id.dialog_attendance_button_going:
                                    respondToEvent(1);
                                    mResponse = Event.Response.YES;
                                    break;
                                case R.id.dialog_attendance_button_maybe:
                                    respondToEvent(3);
                                    mResponse = Event.Response.MAYBE;
                                    break;
                                case R.id.dialog_attendance_button_declined:
                                    respondToEvent(2);
                                    mResponse = Event.Response.NO;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
            );
            dialog.show();
        }
    };

    private void respondToEvent(int response){
        final BaseRequest.OnRequestListener requestListener =  new BaseRequest.OnRequestListener() {
            @Override
            public void onError(int id, ErrorCode errorCode) {
                Toast.makeText(mContext, "Error answering event", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int id, Result result) {
                setMyResponse();
            }

            @Override
            public void onProcessing() {
            }
        };

        new EventResponseRequest(mEvent.getId(), response, mSite.mSiteUrl)
                .withContext((Activity)mContext)
                .setOnRequestListener(requestListener)
                .run();
    }

    private class CategoryListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mEvent.mCategories != null ? mEvent.mCategories.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mEvent.mCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Category category = mDatabase.getCategoryById(mEvent.mCategories.get(position));
            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            view.mItemTitle.setText(category.mName);
            view.mItemSelected.setVisibility(View.GONE);
            view.mItemDot.setTextColor(category.getColor());

            return view;
        }
    }

    private class ResourceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mEvent.mResources != null ? mEvent.mResources.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mEvent.mResources.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Resource resource = mDatabase.getResourceById(mEvent.mResources.get(position));

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            view.mItemTitle.setText(resource.mName);
            view.mItemSelected.setVisibility(View.GONE);
            view.mItemDot.setTextColor(resource.getColor());

            return view;
        }
    }

    private class UsersListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mEvent.mUsers != null ? mEvent.mUsers.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mEvent.mUsers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OtherUser user = mDatabase.getUserById(mEvent.mUsers.get(position));

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            view.mItemTitle.setText(user.mName);
            view.mItemSelected.setVisibility(View.VISIBLE);
            view.mItemDot.setVisibility(View.GONE);
            Event.Response mResponse = Event.Response.MAYBE;
            for(AttendenceStatus status : mEvent.mAttendenceStatus){
                if(status.getUser() == user.getId()){
                    mResponse = Event.Response.values()[status.getStatus()];
                }
            }
            switch (mResponse){
                case YES:
                    view.mItemSelected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.event_attendance_going));
                    break;
                case MAYBE:
                    view.mItemSelected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.event_attendance_maybe));
                    break;
                case NO:
                    view.mItemSelected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.event_attendance_declined));
                    break;
                default:
                    view.mItemSelected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.event_attendance_maybe));
                    break;
            }
            view.mItemImage.setVisibility(View.VISIBLE);
            if(!user.mPictureUrl.isEmpty()) {
                Picasso.with(mContext)
                        .load(user.mPictureUrl)
                        .into(view.mItemImage);
            }
            return view;
        }
    }
}
