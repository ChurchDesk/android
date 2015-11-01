package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

/**
 * Created by Martin on 05/06/2015.
 */
public class EventResponseRequest extends PostRequest<Object> {

    public EventResponseRequest(int eventId, String response, String site) {
        super(URLUtils.getCreateResponseUrl(eventId, response, site));
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        return null;
    }

    @Override
    protected Object parseHttpResponseBody(String body) throws ParserException {
        return null;
    }
}
