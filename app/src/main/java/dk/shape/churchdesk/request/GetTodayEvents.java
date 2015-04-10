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
        int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        Calendar eventStartDay = Calendar.getInstance();
        Calendar eventEndDay = Calendar.getInstance();

        for (Event event : super.parseHttpResponseBody(body)) {
            eventStartDay.setTime(event.mStartDate);
            eventEndDay.setTime(event.mEndDate);

            int startDay = eventStartDay.get(Calendar.DAY_OF_YEAR);
            int endDay = eventEndDay.get(Calendar.DAY_OF_YEAR);
            if (nowDay == startDay)
                event.setPartOfEvent(Event.EventPart.FIRST_DAY);
            else if (nowDay == endDay)
                event.setPartOfEvent(Event.EventPart.LAST_DAY);
            else if (startDay < nowDay  && nowDay < endDay)
                event.setPartOfEvent(Event.EventPart.INTERMEDIATE_DAY);
            else
                continue;
            events.add(event);
        }
        return events;
    }
}
