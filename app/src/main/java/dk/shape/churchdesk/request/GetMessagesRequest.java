package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class GetMessagesRequest extends GetRequest<List<Message>> {

    public GetMessagesRequest(Date date) {
        super(URLUtils.getMessages(date));
    }

    @Override
    protected List<Message> parseHttpResponseBody(String body) throws ParserException {
        return parse(new TypeToken<List<Message>>() {}, body);
    }
}
