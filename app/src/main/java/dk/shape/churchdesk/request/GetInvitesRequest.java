package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
            if (event.hasNoAnswer()) {
                // Modify the dates.
                TimeZone tz = TimeZone.getDefault(); //dealing with timezone
                if (event.mStartDate != null && event.mStartDate instanceof Date) {
                    event.mStartDate.setTime(event.mStartDate.getTime() + tz.getOffset(event.mStartDate.getTime()));
                }

                if (event.mEndDate != null && event.mEndDate instanceof Date) {
                    event.mEndDate.setTime(event.mEndDate.getTime() + tz.getOffset(event.mStartDate.getTime()));
                }

                events.add(event);
            }
        }
        return events;
    }
}
