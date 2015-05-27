package dk.shape.churchdesk.request;

import java.util.List;

import dk.shape.churchdesk.entity.Comment;
import dk.shape.churchdesk.entity.CommentObj;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class GetMessageCommentsRequest extends GetRequest<List<Comment>> {

    public GetMessageCommentsRequest(int messageId, String site) {
        super(URLUtils.getMessageComments(messageId, site));
    }

    @Override
    protected List<Comment> parseHttpResponseBody(String body) throws ParserException {
        CommentObj obj = parse(CommentObj.class, body);
        return obj.mComments;
    }
}
