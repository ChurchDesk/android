package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by Martin on 01/06/2015.
 */
public class CreateEventRequest extends PostRequest<Object>{

    private EventParameter mEventObj;

    public CreateEventRequest(EventParameter event) {
        super(URLUtils.getCreateEventUrl(event.mSite));
        mEventObj = event;
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        String data = parse(mEventObj);
        return RequestBody.create(json, data);
    }

    @Override
    protected Object parseHttpResponseBody(String body) throws ParserException {
        return null;
    }

    public static class EventParameter {

        public EventParameter(String type, String site, int groupId, String title, boolean isAllday, boolean sendNotifications, boolean isAllowDoubleBooking,
                              Date endDate, Date startDate, String visibility, List<Integer> resources, List<Integer> users,
                              String location, String price, String contributor, List<Integer> eventCategories, String internalNote, String description, String substitute, String comments) {
            this.mSite = site;
            this.mGroupId = groupId;
            this.mTitle = title;
            this.isAllDay = isAllday;
            this.isAllowDoubleBooking = isAllowDoubleBooking;

            // Convert date to UTC.
            Calendar startD = Calendar.getInstance();
            Calendar endD = Calendar.getInstance();
            this.mStartDate = startDate;
            this.mStartDate.setTime(this.mStartDate.getTime() - startD.getTimeZone().getRawOffset());

            this.mEndDate = endDate;
            this.mEndDate.setTime(this.mEndDate.getTime() - endD.getTimeZone().getRawOffset());

            this.mResources = resources;
            this.mVisibility = visibility;
            this.mUsers = users;
            this.mLocation = location;
            this.mPrice = price;
            this.mContributor = contributor;
            this.mInternalNote = internalNote;
            this.mDescription = description;
            this.mType = type;
            this.mSendNotifications = sendNotifications;
            // Get the first category as main.
            this.mMainCategory = eventCategories.get(0);
            this.mEventCategories = eventCategories;
            if (type == "absence"){
                this.mSubstitute = substitute;
                this.mComment = comments;
            }

        }

        @SerializedName("organizationId")
        public String mSite;

        @SerializedName("groupId")
        public int mGroupId;

        @SerializedName("title")
        public String mTitle;

        @SerializedName("allDay")
        public Boolean isAllDay;

        @SerializedName("allowDoubleBooking")
        public Boolean isAllowDoubleBooking;

        @SerializedName("endDate")
        public Date mEndDate;

        @SerializedName("startDate")
        public Date mStartDate;

        @SerializedName("visibility")
        public String mVisibility;

        @SerializedName("resources")
        public List<Integer> mResources;

        @SerializedName("users")
        public List<Integer> mUsers;

        @SerializedName("location")
        public String mLocation;

        @SerializedName("price")
        public String mPrice;

        @SerializedName("contributor")
        public String mContributor;

        @SerializedName("taxonomies")
        public List<Integer> mEventCategories;

        @SerializedName("internalNote")
        public String mInternalNote;

        @SerializedName("description")
        public String mDescription;

        @SerializedName("type")
        public String mType;

        @SerializedName("substitute")
        public String mSubstitute;

        @SerializedName("absenceComment")
        public String mComment;

        @SerializedName("mainCategory")
        public Integer mMainCategory;

        @SerializedName("sendNotifications")
        public boolean mSendNotifications;

    }
}
