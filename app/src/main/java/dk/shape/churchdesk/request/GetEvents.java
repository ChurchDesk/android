package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;
import static dk.shape.churchdesk.util.MapUtils.merge;

/**
 * Created by steffenkarlsson on 31/03/15.
 */
public class GetEvents extends GetRequest<SortedMap<Long, List<Event>>> {

    protected GetEvents(String url) {
        super(url);
    }

    public GetEvents(int year, int month) {
        super(URLUtils.getEventsUrl(year, month));
    }

    @Override
    protected SortedMap<Long, List<Event>> parseHttpResponseBody(String body) throws ParserException {
        SortedMap<Long, List<Event>> eventsMap = new TreeMap<>();
        for (Event event : parse(new TypeToken<List<Event>>() {}, body))
            eventsMap = merge(eventsMap, event.convertToMultipleEvents());

        return eventsMap;
    }
}
