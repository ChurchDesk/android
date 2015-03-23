package dk.shape.churchdesk.request;

import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class GetUserRequest extends GetRequest<User> {

    public GetUserRequest() {
        super(URLUtils.getUserUrl());
    }

    @Override
    protected User parseHttpResponseBody(String body) throws ParserException {
        return parse(User.class, body);
    }
}
