package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;


import dk.shape.churchdesk.network.DeleteRequest;
import dk.shape.churchdesk.network.ParserException;

/**
 * Created by chirag on 11/12/15.
 */
public class DeletePushNotificationTokenRequest extends DeleteRequest<Boolean> {

    public DeletePushNotificationTokenRequest(String token) {
        super(URLUtils.getDeletePushNotificationTokenUrl(token));
    }

    @Override
    protected Boolean parseHttpResponseBody(String body) throws ParserException {
        return true;
    }
}
