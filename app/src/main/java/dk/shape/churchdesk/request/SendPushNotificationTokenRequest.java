package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
public class SendPushNotificationTokenRequest extends PostRequest<Object> {

    public SendPushNotificationTokenRequest(String token, String devType) {
        super(URLUtils.getSendPushNotificationTokenUrl(token, devType));
    }

    @Override
    protected Object parseHttpResponseBody(String body) throws ParserException {
        return null;
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        return null;
    }
}
