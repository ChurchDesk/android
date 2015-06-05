package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PutRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by Martin on 05/06/2015.
 */
public class EditEventRequest extends PutRequest<Object>{

    CreateEventRequest.EventParameter mEventObj;

    public EditEventRequest(int eventId, String site, CreateEventRequest.EventParameter parameter) {
        super(URLUtils.getEditEventUrl(eventId, site));
        this.mEventObj = parameter;
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

}
