package dk.shape.churchdesk.request;

import android.content.Context;

import dk.shape.churchdesk.entity.AccessToken;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 31/03/15.
 */
public class GetTokenRequest extends GetRequest<AccessToken> {

    public GetTokenRequest(Context context) {
        super(URLUtils.getTokenUrl(context));
    }

    @Override
    protected AccessToken parseHttpResponseBody(String body) throws ParserException {
        return parse(AccessToken.class, body);
    }
}
