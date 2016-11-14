package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 30/03/15.
 */

public class CreateMessageRequest extends PostRequest<Object>{

    private MessageParameter mMessageObj;

    public CreateMessageRequest(MessageParameter messageObj) {
        super(URLUtils.getCreateMessageUrl());
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

        public MessageParameter(String site, Integer groupId, String title, String body) {
            this.mSite = site;
            this.mGroupId = groupId;
            this.mTitle = title;
            this.mBody = body;
        }

        @SerializedName("organizationId")
        public String mSite;

        @SerializedName("groupId")
        public Integer mGroupId;

        @SerializedName("title")
        public String mTitle;

        @SerializedName("message")
        public String mBody;
    }
}
