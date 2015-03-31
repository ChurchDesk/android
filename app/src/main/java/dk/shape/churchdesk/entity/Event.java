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

    public EventType getType() {
        return EventType.valueOf(mType.toUpperCase());
    }
}
