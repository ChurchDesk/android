package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import dk.shape.churchdesk.entity.Holyday;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 31/03/15.
 */
public class GetHolydays extends GetRequest<List<Holyday>> {

    public GetHolydays(int year, String language) {
        super(URLUtils.getHolydayUrl(year, language));
    }

    @Override
    protected List<Holyday> parseHttpResponseBody(String body) throws ParserException {
        return parse(new TypeToken<List<Holyday>>() {}, body);
    }
}
