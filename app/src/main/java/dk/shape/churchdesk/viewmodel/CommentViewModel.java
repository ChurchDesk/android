package dk.shape.churchdesk.viewmodel;

import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Comment;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.CommentView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by root on 5/27/15.
 */
public class CommentViewModel extends ViewModel<CommentView> {

    public interface CommentViewModelListener {
        void onEditClicked(Comment comment);
        void onDeleteClicked(Comment comment);
    }

    private final CommentViewModelListener _listener;
    private final Comment mComment;

    public CommentViewModel(CommentViewModelListener listener, Comment comment) {
        _listener = listener;
        this.mComment = comment;
    }

    public Comment getComment() {
        return mComment;
    }

    @Override
    public void bind(final CommentView commentView) {
        DatabaseUtils db = DatabaseUtils.getInstance();

        OtherUser otherUser = db.getUserById(mComment.mAuthorId);
        if (otherUser != null) {
            commentView.mAuthorName.setText(otherUser.mName);
            if (otherUser.mPictureUrl != null && !otherUser.mPictureUrl.isEmpty()) {
                Picasso.with(commentView.getContext())
                        .load(otherUser.mPictureUrl)
                        .into(commentView.mAuthorImage);
            }
            else {
                //commentView.mAuthorImage.setImageResource(R.color.default_background);
                Picasso.with(commentView.getContext())
                        .load(R.drawable.user_default)
                        .into(commentView.mAuthorImage);
            }
        }
        else {
            commentView.mAuthorName.setText("");
            commentView.mAuthorImage.setImageResource(R.color.default_background);
        }

        commentView.mTimeAgo.setText(DateUtils.getRelativeTimeSpanString(
                mComment.mCreated.getTime(), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS));

        if (mComment != null && mComment.mBody != null) {
            commentView.mCommentBody.setText(Html.fromHtml(mComment.mBody).toString());
        }

        if(canEdit()) {

            commentView.actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(commentView.getContext(), v);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch(menuItem.getItemId()) {
                                case R.id.action_delete:
                                    _listener.onDeleteClicked(mComment);
                                    break;
                                case R.id.action_edit:
                                    _listener.onEditClicked(mComment);
                                    break;
                            }
                            return true;
                        }
                    });

                    popupMenu.inflate(R.menu.pop_over_comment);

                    popupMenu.getMenu().findItem(R.id.action_delete).setVisible(mComment.canDelete);
                    popupMenu.getMenu().findItem(R.id.action_edit).setVisible(mComment.canEdit);

                    popupMenu.show();
                }
            });

            commentView.actionButton.setVisibility(View.VISIBLE);
        }
        else {
            commentView.actionButton.setVisibility(View.GONE);
        }
    }

    private boolean canEdit() {
        return mComment.canDelete || mComment.canEdit;
    }
}
