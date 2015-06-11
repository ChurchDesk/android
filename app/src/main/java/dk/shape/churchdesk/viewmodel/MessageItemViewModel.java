package dk.shape.churchdesk.viewmodel;

import android.text.format.DateUtils;
import android.view.View;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.MessageItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class MessageItemViewModel extends ViewModel<MessageItemView> {

    public interface OnMessageClickListener {
        void onClick(Message message);
    }

    private final OnMessageClickListener mListener;
    private final User mCurrentUser;
    private final Message mMessage;

    public MessageItemViewModel(Message message, User currentUser,
                                OnMessageClickListener listener) {
        this.mMessage = message;
        this.mCurrentUser = currentUser;
        this.mListener = listener;
    }

    @Override
    public void bind(MessageItemView messageItemView) {
        DatabaseUtils db = DatabaseUtils.getInstance();

        Group group = db.getGroupById(mMessage.mGroupId);
        messageItemView.mGroupTitle.setText(group != null ? group.mName : "");

        Site site = null;
        if (!mCurrentUser.isSingleUser())
            site = mCurrentUser.getSiteById(mMessage.mSiteUrl);
        messageItemView.mSiteTitle.setText(site != null ? site.mSiteName : "");

        OtherUser otherUser = mMessage.mCommentCount == 0 ? db.getUserById(mMessage.mAuthorId) : db.getUserById(mMessage.mLastCommentAuthorId);
        messageItemView.mUsername.setText(otherUser != null ? otherUser.mName : "");

        messageItemView.mSubject.setText(mMessage.mCommentCount > 0
                ? messageItemView.getContext().getString(R.string.is_answer, mMessage.mMessageLine)
                : mMessage.mMessageLine);
        messageItemView.mTimeAgo.setText(DateUtils.getRelativeTimeSpanString(
                mMessage.mLastActivity.getTime(), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS));
        messageItemView.mUnread.setVisibility(mMessage.hasBeenRead
                ? View.GONE : View.VISIBLE);

        messageItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(mMessage);
            }
        });
    }
}
