package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class GetInvitesRequest extends GetRequest<List<Event>> {

    public GetInvitesRequest() {
        super(URLUtils.getInvitesUrl());
    }

    @Override
    protected List<Event> parseHttpResponseBody(String body) throws ParserException {
        List<Event> events = new ArrayList<>();
        for (Event event : parse(new TypeToken<List<Event>>() {}, body)) {
            if (event.hasNoAnswer())
                events.add(event);
        }
        return events;
    }
}
