package dk.shape.churchdesk.request;

import android.content.Context;

import dk.shape.churchdesk.entity.AccessToken;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class RefreshTokenRequest extends GetRequest<AccessToken> {

    public RefreshTokenRequest(Context context, String refreshToken) {
        super(URLUtils.getRefreshTokenUrl(context, refreshToken));
    }

    @Override
    protected AccessToken parseHttpResponseBody(String body) throws ParserException {
        AccessToken token = parse(AccessToken.class, body);
        return token;
    }
}
