package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

import java.util.Calendar;

import dk.shape.churchdesk.entity.Comment;
import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 30/03/15.
 */

public class CreateCommentRequest extends PostRequest<Comment>{

    private CommentParameter mCommentObj;

    public CreateCommentRequest(CommentParameter commentObj) {
        super(URLUtils.getCreateMessageCommentUrl(commentObj.mTargetId));
        this.mCommentObj = commentObj;
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        String data = parse(mCommentObj);
        return RequestBody.create(json, data);
    }

    @Override
    protected Comment parseHttpResponseBody(String body) throws ParserException {
        // When we create the comment, we did not get any response.
        // Lets make sure we create new comment class object.
        Comment comment = parse(Comment.class, body);
        comment.canEdit = true;
        comment.canDelete = true;
        comment.mBody = mCommentObj.mBody;
        comment.mTargetId = mCommentObj.mTargetId;
        comment.mCreated = Calendar.getInstance().getTime();
        return comment;
    }

    public static class CommentParameter {

        public CommentParameter(String site, String body, int messageId) {
            this.mSite = site;
            this.mTargetId = messageId;
            this.mBody = body;
        }

        @SerializedName("organizationId")
        public String mSite;

        @SerializedName("targetId")
        public int mTargetId;

        @SerializedName("body")
        public String mBody;
    }
}
