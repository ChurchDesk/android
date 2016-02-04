package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TimeZone;
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

        TimeZone tz = TimeZone.getDefault();
        for (Event event : parse(new TypeToken<List<Event>>() {}, body)) {
            // Modify the dates.
            if (event.mStartDate != null && event.mStartDate instanceof Date) {
                event.mStartDate.setTime(event.mStartDate.getTime() + tz.getOffset(event.mStartDate.getTime()));
            }
            if (event.mEndDate != null && event.mEndDate instanceof Date) {
                event.mEndDate.setTime(event.mEndDate.getTime() + tz.getOffset(event.mEndDate.getTime()));
            }
            eventsMap = merge(eventsMap, event.convertToMultipleEvents());
        }
        return eventsMap;
    }
}
