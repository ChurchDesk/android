package dk.shape.churchdesk.viewmodel;

import android.text.format.DateUtils;

import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.MessageView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class MessageViewModel extends ViewModel<MessageView> {

    private final User mCurrentUser;
    private final Message mMessage;

    public MessageViewModel(User currentUser, Message message) {
        this.mCurrentUser = currentUser;
        this.mMessage = message;
    }

    @Override
    public void bind(MessageView messageView) {
        DatabaseUtils db = DatabaseUtils.getInstance();
        // TODO: Load imageView

        OtherUser otherUser = db.getUserById(mMessage.mAuthorId);
        messageView.mAuthorName.setText(otherUser != null ? otherUser.mName : "");

        Group group = db.getGroupById(mMessage.mGroupId);
        messageView.mGroupTitle.setText(group != null ? group.mName : "");

        Site site = null;
        if (!mCurrentUser.isSingleUser())
            site = mCurrentUser.getSiteById(mMessage.mSiteUrl);
        messageView.mSiteTitle.setText(site != null ? site.mSiteName : "");

        messageView.mTimeAgo.setText(DateUtils.getRelativeTimeSpanString(
                mMessage.mLastActivity.getTime(), System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));

        messageView.mMessageTitle.setText(mMessage.mTitle);
        messageView.mMessageBody.setText(mMessage.mMessageLine);
    }
}
