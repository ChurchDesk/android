package dk.shape.churchdesk.request;

import dk.shape.churchdesk.entity.Database;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class GetDatabaseRequest extends GetRequest<Database>{

    public GetDatabaseRequest() {
        super(URLUtils.getDatabaseUrl());
    }

    @Override
    protected Database parseHttpResponseBody(String body) throws ParserException {
        return parse(Database.class, body);
    }
}
