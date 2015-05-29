package dk.shape.churchdesk.request;

import dk.shape.churchdesk.entity.CommentObj;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class GetMessageCommentsRequest extends GetRequest<CommentObj> {

    public GetMessageCommentsRequest(int messageId, String site) {
        super(URLUtils.getMessageComments(messageId, site));
    }

    @Override
    protected CommentObj parseHttpResponseBody(String body) throws ParserException {
        return parse(CommentObj.class, body);
    }
}
