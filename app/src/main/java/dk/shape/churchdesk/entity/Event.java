package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

/**
 * Created by steffenkarlsson on 31/03/15.
 */

@Parcel
public class Event {

    public enum EventType {
        EVENT, UNKNOWN
    }

    public enum Response {
        NO_ANSWER, YES, NO, MAYBE, UNKNOWN
    }

    public enum EventPart {
        SINGLE_DAY, FIRST_DAY, INTERMEDIATE_DAY, LAST_DAY
    }

    @SerializedName("site")
    public String mSiteUrl;

    @SerializedName("id")
    public String id;

    @SerializedName("authorId")
    public String mAuthorId;

    @SerializedName("groupId")
    public String mGroupId;

    @SerializedName("created")
    public Date mCreatedAt;

    @SerializedName("publish")
    public String mPublish;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("picture")
    public String mPicture;

    @SerializedName("eventCategories")
    public List<Integer> mCategories;

    @SerializedName("allDay")
    public boolean isAllDay;

    @SerializedName("startDate")
    public Date mStartDate;

    @SerializedName("endDate")
    public Date mEndDate;

    @SerializedName("resources")
    public List<Integer> mResources;

    @SerializedName("users")
    public List<Integer> mUsers;

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

    //Other stuff

    @SerializedName("visibility")
    public int mVisibility;

    public EventPart getPartOfEvent() {
        return mPartOfEvent;
    }

    public void setPartOfEvent(EventPart partOfEvent) {
        this.mPartOfEvent = partOfEvent;
    }

    protected transient EventPart mPartOfEvent;

    public EventType getType() {
        return EventType.valueOf(mType.toUpperCase());
    }

    public Response getResponse() {
        if (mResponse == null || mResponse > 3)
            return Response.UNKNOWN;
        return Response.values()[mResponse];
    }

    public boolean hasNoAnswer() {
        return getResponse() == Response.NO_ANSWER;
    }

    public int getId(){
        return Integer.valueOf(id);
    }

    public int getGroupId(){
        return Integer.valueOf(mGroupId);
    }
}
