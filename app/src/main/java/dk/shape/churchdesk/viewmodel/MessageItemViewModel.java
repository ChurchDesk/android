package dk.shape.churchdesk.viewmodel;

import android.text.format.DateUtils;

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

    private final User mCurrentUser;
    private final Message mMessage;

    public MessageItemViewModel(Message message, User currentUser) {
        this.mMessage = message;
        this.mCurrentUser = currentUser;
    }

    @Override
    public void bind(MessageItemView messageItemView) {
        DatabaseUtils db = DatabaseUtils.getInstance();

        Group group = db.getGroupById(mMessage.mGroupId);
        messageItemView.mGroupTitle.setText(group != null ? group.mName : "");

        Site site = mCurrentUser.getSiteById(mMessage.mSiteUrl);
        messageItemView.mSiteTitle.setText(site != null ? site.mSiteName : "");

        OtherUser otherUser = db.getUserById(mMessage.mAuthorId);
        messageItemView.mUsername.setText(otherUser != null ? otherUser.mName : "");

        messageItemView.mSubject.setText(mMessage.mMessageLine);
        messageItemView.mTimeAgo.setText(DateUtils.getRelativeTimeSpanString(
                mMessage.mLastActivity.getTime(), System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));
    }
}
