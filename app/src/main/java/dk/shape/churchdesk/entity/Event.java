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
        FIRST_DAY, INTERMEDIATE_DAY, LAST_DAY
    }

    @SerializedName("id")
    public String id;

    @SerializedName("site")
    public String mSiteUrl;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("type")
    protected String mType;

    @SerializedName("eventCategories")
    public List<Integer> mCategories;

    @SerializedName("allDay")
    public boolean isAllDay;

    @SerializedName("startDate")
    public Date mStartDate;

    @SerializedName("endDate")
    public Date mEndDate;

    @SerializedName("resources")
    protected List<Integer> mResources;

    @SerializedName("users")
    protected List<Integer> mUsers;

    @SerializedName("canEdit")
    public boolean canEdit;

    @SerializedName("canDelete")
    public boolean canDelete;

    @SerializedName("location")
    public String mLocation;

    // If its an event invitation, following attributes are added

    @SerializedName("response")
    protected Integer mResponse;

    @SerializedName("changed")
    public Date mChanged;

    @SerializedName("invitedBy")
    public Integer mInvitedBy;

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
}
