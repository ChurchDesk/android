package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.entity.PushNotification;
import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
public class SavePushNotificationSettingsRequest extends PostRequest<Boolean> {

    private final PushNotification mPushObj;

    public SavePushNotificationSettingsRequest(PushNotification pushNotification) {
        super(URLUtils.getPushNotificationUrl());

        this.mPushObj = pushNotification;
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        String data = parse(mPushObj);
        return RequestBody.create(json, data);
    }

    @Override
    protected Boolean parseHttpResponseBody(String body) throws ParserException {
        return !body.isEmpty();
    }
}
