package dk.shape.churchdesk.request;

import java.util.ArrayList;
import java.util.Calendar;
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
        Calendar now = Calendar.getInstance();
        Calendar eventDay = Calendar.getInstance();

        for (Event event : super.parseHttpResponseBody(body)) {
            eventDay.setTime(event.mStartDate);
            if (now.get(Calendar.DAY_OF_YEAR) == eventDay.get(Calendar.DAY_OF_YEAR))
                events.add(event);
        }
        return events;
    }
}
