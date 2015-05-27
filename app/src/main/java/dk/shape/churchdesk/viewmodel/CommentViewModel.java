package dk.shape.churchdesk.viewmodel;

import android.text.format.DateUtils;

import com.squareup.picasso.Picasso;

import dk.shape.churchdesk.entity.Comment;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.CommentView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by root on 5/27/15.
 */
public class CommentViewModel extends ViewModel<CommentView> {

    private final Comment mComment;

    public CommentViewModel(Comment comment) {
        this.mComment = comment;
    }

    @Override
    public void bind(CommentView commentView) {
        DatabaseUtils db = DatabaseUtils.getInstance();

        OtherUser otherUser = db.getUserById(mComment.mAuthorId);
        if (otherUser != null) {
            commentView.mAuthorName.setText(otherUser.mName);
            if (!otherUser.mPictureUrl.isEmpty())
                Picasso.with(commentView.getContext())
                        .load(otherUser.mPictureUrl)
                        .into(commentView.mAuthorImage);
        } else {
            commentView.mAuthorName.setText("");
        }

        commentView.mTimeAgo.setText(DateUtils.getRelativeTimeSpanString(
                mComment.mCreated.getTime(), System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));

        commentView.mCommentBody.setText(mComment.mBody);
    }
}
