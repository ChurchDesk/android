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
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Category;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.entity.resources.Resource;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.EventResponseRequest;
import dk.shape.churchdesk.request.GetSingleEventRequest;
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
    String mResponse;

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
        if (mEvent.mType.equals("absence"))
            mEventDetailsView.mCategoryLayout.setOnClickListener(mAbsenceCategoriesClickListener);
        else
            mEventDetailsView.mCategoryLayout.setOnClickListener(mCategoriesClickListener);
        mEventDetailsView.mResourcesLayout.setOnClickListener(mResourcesClickListener);
        mEventDetailsView.mUsersLayout.setOnClickListener(mUsersClickListener);
        mEventDetailsView.mDescriptionButton.setOnClickListener(mDescriptionClickListener);
        mEventDetailsView.mNoteButton.setOnClickListener(mNoteClickListener);
        mEventDetailsView.mSubstituteButton.setOnClickListener(mSubstituteClickListener);
        mEventDetailsView.mCommentsButton.setOnClickListener(mCommentsClickListener);
        mEventDetailsView.mAttendanceButton.setOnClickListener(mAttendanceClickListener);
    }

    private void insertData() {
        mEventDetailsView.mTitle.setText(mEvent.mTitle);
        Group tmpGroup = mDatabase.getGroupById(mEvent.getGroupId());
        mEventDetailsView.mGroup.setText(tmpGroup != null ? tmpGroup.mName : "");
        mEventDetailsView.mParish.setText(mUser.getSiteById(mEvent.mSiteUrl).mSiteName);

        //Image
        if(mEvent.mPicture == null || mEvent.mPicture.isEmpty()){
            mEventDetailsView.mImage.setVisibility(View.INVISIBLE);
            mEventDetailsView.mImageGroupSeperator.setVisibility(View.VISIBLE);
            mEventDetailsView.mTitle.setTextColor(Color.BLACK);
        } else {
            mEventDetailsView.mImageGroupSeperator.setVisibility(View.GONE);
            Picasso.with(mContext)
                    .load(mEvent.mPicture)
                    .transform(new TopGradientTransformation())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mEventDetailsView.mImage.setImageBitmap(bitmap);
                            ((BaseLoggedInActivity)mContext).dismissProgressDialog();
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            ((BaseLoggedInActivity)mContext).dismissProgressDialog();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    }); //mEventDetailsView.mImage
            RelativeLayout.LayoutParams imageViewParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            mEventDetailsView.mImage.setLayoutParams(imageViewParams);
            mEventDetailsView.mImage.setVisibility(View.VISIBLE);
            mEventDetailsView.mTitle.setTextColor(Color.WHITE);
        }

        //Time
        insertTimeString();

        //location
        if(mEvent.mLocation == null || mEvent.mLocation.isEmpty()){
            mEventDetailsView.mLocationLayout.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mLocation.setText(mEvent.mLocation);
        }

        // Categories of the event.
        mEventDetailsView.mCategoryView.removeAllViews();

        List <Integer> categoriesList = new ArrayList<>(mEvent.mCategories.keySet());

        for (int i = 0; i < categoriesList.size(); i+=2) {
            Drawable mColorDrawable1 = mContext.getResources().getDrawable(R.drawable.multiselect_circle);
            EventDetailsMultiItemView view = new EventDetailsMultiItemView(mContext);

            Category firstCategory;
            if (mEvent.mType.equals("absence"))
                firstCategory = mDatabase.getAbsenceById(categoriesList.get(i));
            else
                firstCategory = mDatabase.getCategoryById(categoriesList.get(i));
            view.mMultiCategory1.setText(firstCategory == null ? "" : firstCategory.mName);
            if (mColorDrawable1 != null && firstCategory != null) {
                mColorDrawable1.setColorFilter(new PorterDuffColorFilter(firstCategory.getColor(), PorterDuff.Mode.SRC));
            }
            view.mMultiCategory1.setCompoundDrawablesWithIntrinsicBounds(mColorDrawable1, null, null, null);

            if(i+1 < categoriesList.size()) {
                // Add element to the second column.
                Drawable mColorDrawable2 = mContext.getResources().getDrawable(R.drawable.multiselect_circle);
                Category secondCategory = mDatabase.getCategoryById(categoriesList.get(i+1));
                view.mMultiCategory2.setText(secondCategory == null ? "" : secondCategory.mName);
                if (mColorDrawable2 != null && secondCategory != null) {
                    mColorDrawable2.setColorFilter(new PorterDuffColorFilter(secondCategory.getColor(), PorterDuff.Mode.SRC));
                }
                view.mMultiCategory2.setCompoundDrawablesWithIntrinsicBounds(mColorDrawable2, null, null, null);
            }
            mEventDetailsView.mCategoryView.addView(view);
        }

        // Attendance of the event.
        boolean showAttendance = false;

        if (mEvent.mType.equals("event") && mEvent.mUsers != null && mEvent.mUsers.containsKey(Integer.valueOf(mUser.mUserId))) {
            OtherUser myUser = mEvent.mUsers.get(Integer.valueOf(mUser.mUserId));
            mResponse = myUser.sAttending;
            setMyResponse();
            showAttendance = true;
        }


        mEventDetailsView.mAttendanceButton.setVisibility(showAttendance ? View.VISIBLE : View.GONE);
        mEventDetailsView.mCategoryAttendanceSeperator.setVisibility(showAttendance ? View.VISIBLE : View.GONE);

        //Internal stuff
        boolean showInternalLayout = false;

        // Resources
        if(mEvent.mResources == null || mEvent.mResources.isEmpty()){
            mEventDetailsView.mResourcesLayout.setVisibility(View.GONE);
            mEventDetailsView.mResUsersSeperator.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mResourcesView.removeAllViews();

            List <Integer> resourcesList = new ArrayList<>(mEvent.mResources.keySet());

            for (int i = 0; i < resourcesList.size(); i+=2) {

                Drawable mColorDrawable = mContext.getResources().getDrawable(R.drawable.multiselect_circle);
                EventDetailsMultiItemView view = new EventDetailsMultiItemView(mContext);

                Resource firstResource = mDatabase.getResourceById(resourcesList.get(i));
                view.mMultiCategory1.setText(firstResource == null ? "" : firstResource.mName);

                if (mColorDrawable != null && firstResource != null) {
                    mColorDrawable.setColorFilter(new PorterDuffColorFilter(firstResource.getColor(), PorterDuff.Mode.SRC));
                }
                view.mMultiCategory1.setCompoundDrawablesWithIntrinsicBounds(mColorDrawable, null, null, null);

                if (i+1 < resourcesList.size()) {
                    Drawable mColorDrawable2 = mContext.getResources().getDrawable(R.drawable.multiselect_circle);
                    Resource secondResource = mDatabase.getResourceById(resourcesList.get(i+1));

                    view.mMultiCategory2.setText(secondResource == null ? "" : secondResource.mName);
                    if (mColorDrawable2 != null && secondResource != null) {
                        mColorDrawable2.setColorFilter(new PorterDuffColorFilter(secondResource.getColor(), PorterDuff.Mode.SRC));
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

            // Generate the list of the users.
            List <Integer> usersList = new ArrayList<>(mEvent.mUsers.keySet());

            for (int i = 0; i < usersList.size(); i+=2) {
                EventDetailsMultiItemView view = new EventDetailsMultiItemView(mContext);
                OtherUser firstUser = mDatabase.getUserById(usersList.get(i));
                view.mMultiCategory1.setText(firstUser == null ? "" : firstUser.mName);
                view.mMultiCategory1.setCompoundDrawablePadding(0);

                if (i + 1 < usersList.size()) {
                    OtherUser secondUser = mDatabase.getUserById(usersList.get(i+1));
                    view.mMultiCategory2.setText(secondUser == null ? "" : secondUser.mName);
                    view.mMultiCategory2.setCompoundDrawablePadding(0);
                }

                mEventDetailsView.mUsersView.addView(view);

            }

            showInternalLayout = true;
        }

        //Substitute & comments
        if (mEvent.mType.equals("absence")) {
            if (mEvent.mSubstitute == null || mEvent.mSubstitute.isEmpty()) {
                mEventDetailsView.mSubstituteButton.setVisibility(View.GONE);
                mEventDetailsView.mUsersNoteSeperator.setVisibility(View.GONE);
            } else {
                mEventDetailsView.mSubstitute.setText(Html.fromHtml(mEvent.mSubstitute).toString());
                showInternalLayout = true;
            }
            mEventDetailsView.mNoteButton.setVisibility(View.GONE);

            if (mEvent.mComments == null || mEvent.mComments.isEmpty()) {
                mEventDetailsView.mCommentsButton.setVisibility(View.GONE);
                mEventDetailsView.mUsersNoteSeperator.setVisibility(View.GONE);
            } else {
                mEventDetailsView.mComments.setText(Html.fromHtml(mEvent.mComments).toString());
                showInternalLayout = true;
            }
        }
        else {
        //Internal note
            mEventDetailsView.mCommentsButton.setVisibility(View.GONE);
        if(mEvent.mInternalNote == null || mEvent.mInternalNote.isEmpty()){
            mEventDetailsView.mNoteButton.setVisibility(View.GONE);
            mEventDetailsView.mUsersCommentSeparator.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mNote.setText(Html.fromHtml(mEvent.mInternalNote).toString());
            showInternalLayout = true;
        }
            mEventDetailsView.mSubstituteButton.setVisibility(View.GONE);
        }
        mEventDetailsView.mInternalLayout.setVisibility(showInternalLayout ? View.VISIBLE : View.GONE);

        //external stuff
        boolean showExternalLayout = false;

        //Contributor / Person
        if(mEvent.mContributor == null || mEvent.mContributor.isEmpty()){
            mEventDetailsView.mContributorLayout.setVisibility(View.GONE);
            mEventDetailsView.mContributorPriceSeperator.setVisibility(View.GONE);
        } else {
            mEventDetailsView.mContributor.setText(mEvent.mContributor);
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

            // Clean html tags before rendering it.
            mEventDetailsView.mDescription.setText(Html.fromHtml(mEvent.mDescription).toString());
            showExternalLayout = true;
        }
        mEventDetailsView.mExternalLayout.setVisibility(showExternalLayout ? View.VISIBLE : View.GONE);

        //Visibility of the event
        if (!mEvent.mType.equals("absence")) {
            if (mEvent.mVisibility.equals("web"))
                mEventDetailsView.mVisibility.setText(R.string.event_details_visibility_website);
            else if (mEvent.mVisibility.equals("group"))
                mEventDetailsView.mVisibility.setText(R.string.event_details_visibility_group);
            else
                mEventDetailsView.mVisibility.setText(R.string.event_details_visibility_draft);
        }
        else
            mEventDetailsView.mVisibilityButton.setVisibility(View.GONE);
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
            case "yes":
                mEventDetailsView.mAttendance.setText(R.string.event_details_response_going);
                mEventDetailsView.mAttendance.setTextColor(Color.GREEN);
                break;
            case "maybe":
                mEventDetailsView.mAttendance.setText(R.string.event_details_response_maybe);
                mEventDetailsView.mAttendance.setTextColor(Color.GRAY);
                break;
            case "no":
                mEventDetailsView.mAttendance.setText(R.string.event_details_response_not_going);
                mEventDetailsView.mAttendance.setTextColor(Color.RED);
                break;
            default:
                mEventDetailsView.mAttendance.setText(R.string.event_details_response_no_answer);
                mEventDetailsView.mAttendance.setTextColor(Color.BLACK);
                break;
        }
    }

    private void insertTimeString(){
        //set time of event
        String timeOfEvent;
        TimeZone tz = TimeZone.getDefault();
        Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(mEvent.mStartDate.getTime() + tz.getOffset(mEvent.mStartDate.getTime()));
        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(mEvent.mEndDate.getTime() + tz.getOffset(mEvent.mEndDate.getTime()));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            builder.setTitle(R.string.event_details_location_dialog_title);
            builder.setMessage(mContext.getString(R.string.event_details_location_dialog_message, mEvent.mLocation));
            builder.setPositiveButton(R.string.event_details_location_dialog_button_positive , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + mEvent.mLocation);
                    Intent i = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    i.setPackage("com.google.android.apps.maps");
                    mContext.startActivity(i);
                }
            });
            builder.setNegativeButton(R.string.event_details_location_dialog_button_negative, new DialogInterface.OnClickListener() {
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

    private LinearLayout.OnClickListener mAbsenceCategoriesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new AbsenceCategoryListAdapter(), R.string.event_details_categories_dialog);
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

    private LinearLayout.OnClickListener mResourcesClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new ResourceListAdapter(), R.string.event_details_resource_dialog);
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

    private LinearLayout.OnClickListener mUsersClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    new UsersListAdapter(), R.string.event_details_users_dialog);
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

    private LinearLayout.OnClickListener mDescriptionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    null, R.string.event_details_description_dialog);
            dialog.showOnlyText(Html.fromHtml(mEvent.mDescription).toString());
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
            dialog.showOnlyText(Html.fromHtml(mEvent.mInternalNote).toString());
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

    private LinearLayout.OnClickListener mSubstituteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    null, R.string.new_absence_hint_substitute);
            dialog.showOnlyText(Html.fromHtml(mEvent.mSubstitute).toString());
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

    private LinearLayout.OnClickListener mCommentsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                    null, R.string.new_absence_hint_comments);
            dialog.showOnlyText(Html.fromHtml(mEvent.mComments).toString());
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
                    mContext.getString(R.string.event_details_attendance_message, mEvent.mTitle),
                    mEvent.getId(), mEvent.mSiteUrl);
            dialog.addOnClickListeners(
                    new CustomTextView.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            switch (v.getId()){
                                case R.id.dialog_attendance_button_going:
                                    respondToEvent(Event.Response.YES.toString());
                                    mResponse = Event.Response.YES.toString();
                                    break;
                                case R.id.dialog_attendance_button_maybe:
                                    respondToEvent(Event.Response.MAYBE.toString());
                                    mResponse = Event.Response.MAYBE.toString();
                                    break;
                                case R.id.dialog_attendance_button_declined:
                                    respondToEvent(Event.Response.NO.toString());
                                    mResponse = Event.Response.NO.toString();
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

    private void respondToEvent(final String response){
        final BaseRequest.OnRequestListener refreshEventListener = new BaseRequest.OnRequestListener() {
            @Override
            public void onError(int id, ErrorCode errorCode) {
            }

            @Override
            public void onSuccess(int id, Result result) {
                if (result.statusCode == HttpStatus.SC_OK && result.response != null) {
                    //mEvent.mAttendenceStatus = ((Event)result.response).mAttendenceStatus;
                    setMyResponse();
                }
            }

            @Override
            public void onProcessing() {
            }
        };

        final BaseRequest.OnRequestListener requestListener =  new BaseRequest.OnRequestListener() {
            @Override
            public void onError(int id, ErrorCode errorCode) {
                Toast.makeText(mContext, R.string.event_details_attendance_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int id, Result result) {
                new GetSingleEventRequest(mEvent.getId(), mSite.mSiteUrl)
                        .withContext((BaseActivity)mContext)
                                .setOnRequestListener(refreshEventListener)
                                .run();
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

            ArrayList<Integer> categoryListKeys = new ArrayList<>(mEvent.mCategories.keySet());
            Category category = mDatabase.getCategoryById(categoryListKeys.get(position));

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            if(category != null) {
                view.mItemTitle.setText(category.mName);
                view.mItemDot.setTextColor(category.getColor());
            } else {
                view.mItemDot.setVisibility(View.INVISIBLE);
            }
            view.mItemSelected.setVisibility(View.GONE);
            return view;
        }
    }

    private class AbsenceCategoryListAdapter extends BaseAdapter {

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

            ArrayList<Integer> categoryListKeys = new ArrayList<>(mEvent.mCategories.keySet());
            Category category = mDatabase.getAbsenceById(categoryListKeys.get(position));

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            if(category != null) {
                view.mItemTitle.setText(category.mName);
                view.mItemDot.setTextColor(category.getColor());
            } else {
                view.mItemDot.setVisibility(View.INVISIBLE);
            }
            view.mItemSelected.setVisibility(View.GONE);
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

            ArrayList<Integer> resourcesList = new ArrayList<>(mEvent.mResources.keySet());
            Resource resource = mDatabase.getResourceById(resourcesList.get(position));

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            if(resource != null) {
                view.mItemTitle.setText(resource.mName);
                view.mItemDot.setTextColor(resource.getColor());
            } else {
                view.mItemDot.setVisibility(View.INVISIBLE);
            }
            view.mItemSelected.setVisibility(View.GONE);

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

            List <Integer> userList = new ArrayList<>(mEvent.mUsers.keySet());
            OtherUser user = mDatabase.getUserById(userList.get(position));

            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            if(user != null) {
                view.mItemTitle.setText(user.mName);
                view.mItemSelected.setVisibility(View.VISIBLE);

                OtherUser mainUserObject = mEvent.mUsers.get(Integer.valueOf(user.id));

                switch (mainUserObject.sAttending) {
                    case "yes":
                        view.mItemSelected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.event_attendance_going));
                        break;
                    case "maybe":
                        view.mItemSelected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.event_attendance_maybe));
                        break;
                    case "no":
                        view.mItemSelected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.event_attendance_declined));
                        break;
                    default:
                        view.mItemSelected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.event_attendance_noreply));
                        break;
                }
                view.mItemImage.setVisibility(View.VISIBLE);
                if (user.mPictureUrl != null && !user.mPictureUrl.isEmpty()) {
                    Picasso.with(mContext)
                            .load(user.mPictureUrl)
                            .into(view.mItemImage);
                } else {
                    Picasso.with(mContext)
                            .load(R.drawable.user_default)
                            .into(view.mItemImage);
                }
            } else {
                view.mItemImage.setVisibility(View.GONE);
                view.mItemSelected.setVisibility(View.GONE);
            }
            view.mItemDot.setVisibility(View.GONE);
            return view;
        }
    }

    private class TopGradientTransformation implements Transformation{

        @Override
        public Bitmap transform(Bitmap source) {
            Shader[] shaders = new Shader[2];

            Bitmap bitmap = Bitmap.createBitmap(source.getWidth(),
                    source.getHeight(),
                    source.getConfig());


            shaders[0] = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            shaders[1] = new LinearGradient(0,
                    source.getHeight()/2,
                    0,
                    source.getHeight(),
                    Color.BLACK,
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP);
            ComposeShader composeShader = new ComposeShader(shaders[0],
                    shaders[1],
                    PorterDuff.Mode.DST_IN);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(composeShader);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.BLACK);
            canvas.drawPaint(paint);

            source.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "";
        }
    }
}
