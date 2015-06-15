package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by Martin on 12/06/2015.
 */
public class GetMessagesSearchRequest extends GetRequest<List<Message>> {

    public GetMessagesSearchRequest(Date date, String query) {
        super(URLUtils.getMessagesSearchUrl(date, query));
    }

    @Override
    protected List<Message> parseHttpResponseBody(String body) throws ParserException {
        return parse(new TypeToken<List<Message>>() {}, body);
    }
}