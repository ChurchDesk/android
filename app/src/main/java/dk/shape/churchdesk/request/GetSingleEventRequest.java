package dk.shape.churchdesk.request;

import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by Martin on 04/06/2015.
 */
public class GetSingleEventRequest extends GetRequest<Event> {

    public GetSingleEventRequest(int eventId, String site) {
        super(URLUtils.getSingleEvent(eventId, site));
    }

    @Override
    protected Event parseHttpResponseBody(String body) throws ParserException {
        return parse(Event.class, body);
    }
}
