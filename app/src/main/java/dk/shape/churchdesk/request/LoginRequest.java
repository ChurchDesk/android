package dk.shape.churchdesk.request;

import android.content.Context;

import dk.shape.churchdesk.entity.AccessToken;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class LoginRequest extends GetRequest<AccessToken> {

    public LoginRequest(Context context, String username, String password) {
        super(URLUtils.getLoginUrl(context, username, password));
    }

    @Override
    protected AccessToken parseHttpResponseBody(String body) throws ParserException {
        return parse(AccessToken.class, body);
    }
}
