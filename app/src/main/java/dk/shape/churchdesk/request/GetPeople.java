package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by chirag on 24/01/2017.
 */
public class GetPeople extends GetRequest<List<Person>> {

    public GetPeople(String organisationId, int segmentId) {
        super(URLUtils.getPeopleUrl(organisationId, segmentId));
    }

    @Override
    protected List<Person> parseHttpResponseBody(String body) throws ParserException {
        List<Person> sortedList = parse(new TypeToken<List<Person>>() {}, body);
        Collections.sort(sortedList, new Comparator<Person>() {
            public int compare(Person p1, Person p2) {
                if (p1.mFullName == null || p1.mFullName.length() == 0)
                    p1.mFullName = "unknown";
                if (p2.mFullName == null || p2.mFullName.length() == 0)
                    p2.mFullName = "unknown";

                return p1.mFullName.compareTo(p2.mFullName);
            }
        });
        return sortedList;
    }
}
