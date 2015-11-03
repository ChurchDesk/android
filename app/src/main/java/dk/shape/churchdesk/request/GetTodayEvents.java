package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import org.parceler.apache.commons.lang.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 31/03/15.
 */
public class GetTodayEvents extends GetRequest<List<Event>> {

    public GetTodayEvents() {
        super(URLUtils.getTodayEventsUrl());
    }

    @Override
    protected List<Event> parseHttpResponseBody(String body) throws ParserException {
        List<Event> events = new ArrayList<>();
        int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        Calendar eventStartDay = Calendar.getInstance();
        Calendar eventEndDay = Calendar.getInstance();

        for (Event event : parse(new TypeToken<List<Event>>() {}, body)) {

            if (event.mStartDate != null && event.mStartDate instanceof Date) {
                event.mStartDate.setTime(event.mStartDate.getTime() + eventStartDay.getTimeZone().getRawOffset());
            }
            if (event.mEndDate != null && event.mEndDate instanceof Date) {
                event.mEndDate.setTime(event.mEndDate.getTime() + eventEndDay.getTimeZone().getRawOffset());
            }

            eventStartDay.setTime(event.mStartDate);
            eventEndDay.setTime(event.mEndDate);

            int startDay = eventStartDay.get(Calendar.DAY_OF_YEAR);
            int endDay = eventEndDay.get(Calendar.DAY_OF_YEAR);
            if (nowDay == startDay && nowDay == endDay)
                event.setPartOfEvent(Event.EventPart.SINGLE_DAY);
            else if (nowDay == startDay)
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
