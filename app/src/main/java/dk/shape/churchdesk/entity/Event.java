package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dk.shape.churchdesk.entity.resources.Category;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.entity.resources.Resource;
import hirondelle.date4j.DateTime;

/**
 * Created by steffenkarlsson on 31/03/15.
 */

@Parcel
public class Event extends BaseDay {

    @Override
    public Date getDate() {
        return mStartDate;
    }

    // Enumeration list for the calendar entities.
    public enum EventType {
        EVENT, UNKNOWN
    }

    // The possible response types for the event.
    public enum Response {
        NO_ANSWER {
            @Override
            public String toString() {
                return "no-answer";
            }
        }, YES {
            @Override
            public String toString() {
                return "yes";
            }
        }, NO {
            @Override
            public String toString() {
                return "no";
            }
        }, MAYBE {
            @Override
            public String toString() {
                return "maybe";
            }
        }, UNKNOWN {
            @Override
            public String toString() {
                return "unknown";
            }
        }
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
        event.mUsers = mUsers;
        event.mHeaderId = headerId;
        event.setPartOfEvent(part);
        event.mAuthorId = mAuthorId;
        event.mType = mType;
        event.mComments = mComments;
        event.mSubstitute = mSubstitute;
        return event;
    }

    @SerializedName("organizationId")
    public String mSiteUrl;

    @SerializedName("id")
    public String id;

    @SerializedName("authorId")
    public Integer mAuthorId;

    @SerializedName("groups")
    public HashMap<Integer, Group> mgroups;;

    @SerializedName("createdAt")
    public Date mCreatedAt;

    @SerializedName("publish")
    public String mPublish;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("resources")
    public HashMap<Integer, Resource> mResources;

    @SerializedName("users")
    public HashMap<Integer, OtherUser> mUsers;

    @SerializedName("image")
    public String mPicture;

    @SerializedName("taxonomies")
    public HashMap<Integer, Category> mCategories;

    @SerializedName("allDay")
    public boolean isAllDay;

    @SerializedName("startDate")
    public Date mStartDate;

    @SerializedName("endDate")
    public Date mEndDate;

    @SerializedName("description")
    public String mDescription;

    @SerializedName("contributor")
    public String mContributor;

    @SerializedName("internalNote")
    public String mInternalNote;

    @SerializedName("location")
    public String mLocation;

    @SerializedName("price")
    public String mPrice;

    @SerializedName("type")
    public String mType;

    @SerializedName("canEdit")
    public boolean canEdit;

    @SerializedName("canDelete")
    public boolean canDelete;

    // If its an event invitation, following attributes are added
    @SerializedName("attending")
    public String mResponse;

    @SerializedName("updatedAt")
    public Date mChanged;

    @SerializedName("invitedBy")
    public Integer mInvitedBy;

    @SerializedName("visibility")
    public String mVisibility;

    @SerializedName("substitute")
    public String mSubstitute;

    @SerializedName("absenceComment")
    public String mComments;

    public long mHeaderId;

    public boolean isDummy = false;

    public static Event instantiateAsDummy(long headerId) {
        Event event = new Event();
        event.isDummy = true;
        event.mHeaderId = headerId;
        return event;
    }
    //Other stuff

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

    public String getResponse() {
        if (mResponse == null)
            return Response.NO_ANSWER.toString();
        return mResponse;
    }

    public boolean isMyEvent(User me) {
        if (isDummy)
            return false;
        if (mUsers != null && mSiteUrl != null) {
            Site site = me.getSiteById(mSiteUrl);
            String author = this.mAuthorId != null ? this.mAuthorId.toString() : "";
            if (site != null && (me.mUserId.equals(author))) {
                return true;
            }

            if (this.mUsers != null) {
                for (Integer bookedUserId : this.mUsers.keySet()) {
                    if (bookedUserId.equals(Integer.valueOf(me.mUserId))) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public boolean hasNoAnswer() {
        return getResponse().equals(Response.NO_ANSWER.toString());
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
        reset(eDay);

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
                if (!events.containsKey(sDay.getTimeInMillis())) {
                    events.put(sDay.getTimeInMillis(), new ArrayList<Event>());
                }
                events.get(sDay.getTimeInMillis()).add(copy(part, allDay, sDay.getTimeInMillis()));

            if (sDay.getTimeInMillis() == eDay.getTimeInMillis())
                break;

            sDay.add(Calendar.DATE, 1);
            startDate = sDay.getTime();
        } while (sDay.before(eDay) || sDay.equals(eDay) );
        return events;
    }

    public Integer getId(){
        return id == null ? null : Integer.valueOf(id);
    }

    public List<Integer> getGroupIds(){
        List<Integer> groupIds = new ArrayList<>();
        if (!mgroups.isEmpty()){
        for (Integer key: mgroups.keySet()) {
            Group group = mgroups.get(key);
            groupIds.add(group.getId());
            }
        }
        return groupIds;
    }

    public Category getMainCategory() {
        for (Category  cat : this.mCategories.values()) {
            if (cat.mIsMaster) {
                return cat;
            }
        }
        // If the main one is not specified, make sure we will return just first category.
        return this.mCategories.get(0);
    }
}
