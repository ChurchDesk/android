package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

/**
 * Created by steffenkarlsson on 10/04/15.
 */
public class MarkMessageAsReadRequest extends PostRequest<Boolean> {

    public MarkMessageAsReadRequest(Message message) {
        super(URLUtils.getMarkMessageAsReadUrl(message.id, message.mSiteUrl));
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        return RequestBody.create(json, "");
    }

    @Override
    protected Boolean parseHttpResponseBody(String body) throws ParserException {
        return !body.isEmpty();
    }
}
