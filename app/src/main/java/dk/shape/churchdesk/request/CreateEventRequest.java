package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

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
        super(URLUtils.getCreateEventUrl());
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

        public EventParameter(String site, int groupId, String title, boolean isAllday, boolean isAllowDoubleBooking,
                              Date endDate, Date startDate, int visibility, List<Integer> resources, List<Integer> users,
                              String location, String price, String person, List<Integer> eventCategories, String internalNote, String description) {
            this.mSite = site;
            this.mGroupId = groupId;
            this.mTitle = title;
            this.isAllDay = isAllday;
            this.isAllowDoubleBooking = isAllowDoubleBooking;
            this.mEndDate = endDate;
            this.mStartDate = startDate;
            this.isPublish = true;
            this.mResources = resources;
            this.mVisibility = visibility;
            this.mUsers = users;
            this.mLocation = location;
            this.mPrice = price;
            this.mPerson = person;
            this.mEventCategories = eventCategories;
            this.mInternalNote = internalNote;
            this.mDescription = description;
        }

        @SerializedName("site")
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
        public int mVisibility;

        @SerializedName("publish")
        public Boolean isPublish;

        @SerializedName("resources")
        public List<Integer> mResources;

        @SerializedName("users")
        public List<Integer> mUsers;

        @SerializedName("location")
        public String mLocation;

        @SerializedName("price")
        public String mPrice;

        @SerializedName("person")
        public String mPerson;

        @SerializedName("eventCategories")
        public List<Integer> mEventCategories;

        @SerializedName("internalNote")
        public String mInternalNote;

        @SerializedName("description")
        public String mDescription;

    }
}
