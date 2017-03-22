package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.entity.Segment;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by chirag on 24/01/2017.
 */
public class GetSegments  extends GetRequest<List<Segment>> {

    public GetSegments(String organisationId) {
        super(URLUtils.getSegmentsUrl(organisationId));
    }

    @Override
    protected List<Segment> parseHttpResponseBody(String body) throws ParserException {
        List<Segment> sortedList = parse(new TypeToken<List<Segment>>() {}, body);
        Collections.sort(sortedList, new Comparator<Segment>() {
            public int compare(Segment s1, Segment s2) {
                if (s1.mName == null || s1.mName.length() == 0)
                    s1.mName = "unknown";
                if (s2.mName == null || s2.mName.length() == 0)
                    s2.mName = "unknown";

                return s1.mName.compareToIgnoreCase(s2.mName);
            }
        });
        return sortedList;
    }
}
