package dk.shape.churchdesk.viewmodel;

import android.text.format.DateUtils;

import com.squareup.picasso.Picasso;

import dk.shape.churchdesk.entity.CommentObj;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.MessageHeaderView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by root on 5/27/15.
 */
public class MessageHeaderViewModel extends ViewModel<MessageHeaderView> {

    private final User mCurrentUser;
    private final CommentObj mMessage;

    public MessageHeaderViewModel(User currentUser, CommentObj message) {
        this.mCurrentUser = currentUser;
        this.mMessage = message;
    }

    @Override
    public void bind(MessageHeaderView messageHeaderView) {
        DatabaseUtils db = DatabaseUtils.getInstance();

        OtherUser otherUser = db.getUserById(mMessage.mAuthorId);
        if (otherUser != null) {
            messageHeaderView.mAuthorName.setText(otherUser.mName);
            if (!otherUser.mPictureUrl.isEmpty())
                Picasso.with(messageHeaderView.getContext())
                        .load(otherUser.mPictureUrl)
                        .into(messageHeaderView.mAuthorImage);
        } else {
            messageHeaderView.mAuthorName.setText("");
        }

        Group group = db.getGroupById(mMessage.mGroupId);
        messageHeaderView.mGroupTitle.setText(group != null ? group.mName : "");

        Site site = null;
        if (!mCurrentUser.isSingleUser())
            site = mCurrentUser.getSiteById(mMessage.mSiteUrl);
        messageHeaderView.mSiteTitle.setText(site != null ? site.mSiteName : "");

        messageHeaderView.mTimeAgo.setText(DateUtils.getRelativeTimeSpanString(
                mMessage.mChanged.getTime(), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS));

        messageHeaderView.mMessageTitle.setText(mMessage.mTitle);
        messageHeaderView.mMessageBody.setText(mMessage.mBody);
    }
}
