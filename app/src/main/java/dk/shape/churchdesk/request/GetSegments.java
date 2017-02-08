package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

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
        return parse(new TypeToken<List<Segment>>() {}, body);
    }
}
