package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by chirag on 09/03/2017.
 */
public class CreatePeopleMessageRequest extends PostRequest<Object> {

    private MessageParameter mMessageObj;

    public CreatePeopleMessageRequest(MessageParameter messageObj, String organizationId) {
        super(URLUtils.getCreatePeopleMessageUrl(organizationId));
        this.mMessageObj = messageObj;
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        String data = parse(mMessageObj);
        return RequestBody.create(json, data);
    }

    @Override
    protected Object parseHttpResponseBody(String body) throws ParserException {
        return null;
    }

    public static class MessageParameter {

        public MessageParameter(String organizationId, String title, String body, String from, List<HashMap<String, String>> to, String type) {
            this.mOrganizationId = organizationId;
            this.mTitle = title;
            this.mBody = body;
            this.mFrom = from;
            this.mTo = to;
            this.mType = type;
            Calendar calStart = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            this.mScheduled = calStart.getTime();
            this.mScheduled.setTime(this.mScheduled.getTime() - tz.getOffset(this.mScheduled.getTime()));
        }

        @SerializedName("organizationId")
        public String mOrganizationId;

        @SerializedName("title")
        public String mTitle;

        @SerializedName("content")
        public String mBody;

        @SerializedName("from")
        public String mFrom;

        @SerializedName("to")
        public List<HashMap<String, String>> mTo;

        @SerializedName("type")
        public String mType;

        @SerializedName("scheduled")
        public Date mScheduled;
    }
}
