package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 31/03/15.
 */
public class GetEvents extends GetRequest<List<Event>> {

    protected GetEvents(String url) {
        super(url);
    }

    public GetEvents(int year, int month) {
        super(URLUtils.getEventsUrl(year, month));
    }

    @Override
    protected List<Event> parseHttpResponseBody(String body) throws ParserException {
        return parse(new TypeToken<List<Event>>() {}, body);
    }
}
