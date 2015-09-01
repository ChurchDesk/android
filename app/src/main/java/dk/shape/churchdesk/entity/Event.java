package dk.shape.churchdesk.entity;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.shape.churchdesk.entity.resources.Category;

/**
 * Created by steffenkarlsson on 31/03/15.
 */

@Parcel
public class Event extends BaseDay {

    @Override
    public Date getDate() {
        return mStartDate;
    }

    public enum EventType {
        EVENT, UNKNOWN
    }

    public enum Response {
        NO_ANSWER, YES, NO, MAYBE, UNKNOWN
    }

    public enum EventPart {
        SINGLE_DAY, FIRST_DAY, INTERMEDIATE_DAY, LAST_DAY
    }

    private Event copy(EventPart part, boolean isAllDay, long headerId) {
        Event event = new Event();
        event.id = id;
        event.mSiteUrl = mSiteUrl;
        event.mTitle = mTitle;
        event.mCategories = mCategories;
        event.isAllDay = isAllDay;
        event.mStartDate = mStartDate;
        event.mEndDate = mEndDate;
        event.canEdit = canEdit;
        event.canDelete = canDelete;
        event.mLocation = mLocation;
        event.mUsers = new ArrayList<>(mUsers);
        event.mHeaderId = headerId;
        event.setPartOfEvent(part);
        return event;
    }

    @SerializedName("organizationId")
    public String mSiteUrl;

    @SerializedName("id")
    public String id;

    @SerializedName("authorId")
    public String mAuthorId;

    @SerializedName("groupId")
    public String mGroupId;

    @SerializedName("createdAt")
    public Date mCreatedAt;

    @SerializedName("publish")
    public String mPublish;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("resources")
    public List<Integer> mResources;

    @SerializedName("users")
    public List<Integer> mUsers;

    @SerializedName("picture")
    public String mPicture;

    @SerializedName("taxonomies")
    public List<Integer> mCategories;

    @SerializedName("allDay")
    public boolean isAllDay;

    @SerializedName("startDate")
    public Date mStartDate;

    @SerializedName("endDate")
    public Date mEndDate;

    @SerializedName("description")
    public String mDescription;

    @SerializedName("internalNote")
    public String mInternalNote;

    @SerializedName("location")
    public String mLocation;

    @SerializedName("price")
    public String mPrice;

    @SerializedName("person")
    public String mPerson;

    @SerializedName("type")
    public String mType;

    @SerializedName("attendenceStatus")
    public List<AttendenceStatus> mAttendenceStatus;

    @SerializedName("canEdit")
    public boolean canEdit;

    @SerializedName("canDelete")
    public boolean canDelete;

    // If its an event invitation, following attributes are added

    @SerializedName("response")
    public Integer mResponse;

    @SerializedName("changed")
    public Date mChanged;

    @SerializedName("invitedBy")
    public Integer mInvitedBy;

    public long mHeaderId;

    public boolean isDummy = false;

    public static Event instantiateAsDummy(long headerId) {
        Event event = new Event();
        event.isDummy = true;
        event.mHeaderId = headerId;
        return event;
    }
    //Other stuff

    @SerializedName("visibility")
    public int mVisibility;

    public EventPart getPartOfEvent() {
        return mPartOfEvent;
    }

    public void setPartOfEvent(EventPart partOfEvent) {
        this.mPartOfEvent = partOfEvent;
    }

    protected EventPart mPartOfEvent;

    public EventType getType() {
        return EventType.valueOf(mType.toUpperCase());
    }

    public Response getResponse() {
        if (mResponse == null || mResponse > 3)
            return Response.UNKNOWN;
        return Response.values()[mResponse];
    }

    public boolean isMyEvent(User me) {
        if (isDummy)
            return false;
        if (mUsers != null && mSiteUrl != null) {
            Site site = me.getSiteById(mSiteUrl);
            return site != null && mUsers.contains((Integer) site.mUserId);
        }
        return false;
    }

    public boolean hasNoAnswer() {
        return getResponse() == Response.NO_ANSWER;
    }

    public HashMap<Long, List<Event>> convertToMultipleEvents() {
        HashMap<Long, List<Event>> events = new HashMap<>();

        Calendar origSDay = Calendar.getInstance();
        origSDay.setTime(mStartDate);
        Date startDate = origSDay.getTime();
        reset(origSDay);

        Calendar origEDay = Calendar.getInstance();
        origEDay.setTime(mEndDate);
        reset(origEDay);

        Calendar sDay = Calendar.getInstance();
        Calendar eDay = Calendar.getInstance();
        eDay.setTime(mEndDate);
        eDay = reset(eDay);

        do {
            sDay.setTime(startDate);
            sDay = reset(sDay);

            EventPart part = EventPart.SINGLE_DAY;
            boolean allDay = isAllDay;
            if (!sDay.equals(origSDay) && !sDay.equals(origEDay)) {
                allDay = true;
                part = EventPart.INTERMEDIATE_DAY;
            } else if (sDay.equals(origSDay) && !sDay.equals(origEDay))
                part = EventPart.FIRST_DAY;
            else if (sDay.equals(origEDay) && !sDay.equals(origSDay))
                part = EventPart.LAST_DAY;

            if (!events.containsKey(sDay.getTimeInMillis()))
                events.put(sDay.getTimeInMillis(), new ArrayList<Event>());
            events.get(sDay.getTimeInMillis()).add(copy(part, allDay, sDay.getTimeInMillis()));

            if (sDay.getTimeInMillis() == eDay.getTimeInMillis())
                break;

            sDay.add(Calendar.DATE, 1);
            startDate = sDay.getTime();
        } while (sDay.get(Calendar.DATE) <= eDay.get(Calendar.DATE));
        return events;
    }

    public Integer getId(){
        return id == null ? null : Integer.valueOf(id);
    }

    public int getGroupId(){
        return Integer.valueOf(mGroupId);
    }
}
