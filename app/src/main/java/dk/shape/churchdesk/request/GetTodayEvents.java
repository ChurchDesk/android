package dk.shape.churchdesk.request;

import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.List;

import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.network.ParserException;

/**
 * Created by steffenkarlsson on 31/03/15.
 */
public class GetTodayEvents extends GetEvents {

    public GetTodayEvents() {
        super(URLUtils.getTodayEventsUrl());
    }

    @Override
    protected List<Event> parseHttpResponseBody(String body) throws ParserException {
        List<Event> events = new ArrayList<>();
        for (Event event : super.parseHttpResponseBody(body)) {
            if (DateUtils.isToday(event.mStartDate.getTime()))
                events.add(event);
        }
        return events;
    }
}
