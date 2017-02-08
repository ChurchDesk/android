package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by chirag on 24/01/2017.
 */
public class GetPeople extends GetRequest<List<Person>> {

    public GetPeople(String organisationId, List<String> segmentIds) {
        super(URLUtils.getPeopleUrl(organisationId, segmentIds));
    }

    @Override
    protected List<Person> parseHttpResponseBody(String body) throws ParserException {
        return parse(new TypeToken<List<Person>>() {}, body);
    }
}
