package dk.shape.churchdesk.request;

import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by chirag on 23/02/2017.
 */
public class GetSinglePersonRequest extends GetRequest<Person> {

    public GetSinglePersonRequest(int personId, String organizationId) {
        super(URLUtils.getSinglePerson(personId, organizationId));
    }

    @Override
    protected Person parseHttpResponseBody(String body) throws ParserException {
        return parse(Person.class, body);
    }
}
