package dk.shape.churchdesk.viewmodel;

import android.text.format.DateUtils;
import android.view.View;

import java.util.Calendar;
import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Category;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.util.DateAppearanceUtils;
import dk.shape.churchdesk.view.EventItemView;
import dk.shape.churchdesk.view.InvitationItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class InvitationItemViewModel extends ViewModel<InvitationItemView> {

    private final User mCurrentUser;
    private final Event mInvitation;

    public InvitationItemViewModel(Event invitation, User currentUser) {
        this.mInvitation = invitation;
        this.mCurrentUser = currentUser;
    }

    @Override
    public void bind(InvitationItemView invitationItemView) {
        DatabaseUtils db = DatabaseUtils.getInstance();

        Site site = null;
        if (!mCurrentUser.isSingleUser())
            site = mCurrentUser.getSiteById(mInvitation.mSiteUrl);
        invitationItemView.mEventSite.setText(site != null ? site.mSiteName : "");

        invitationItemView.mEventTitle.setText(mInvitation.mTitle);

        List<Category> categories = db.getCategoriesBySiteId(mInvitation.mSiteUrl,
                String.valueOf(mInvitation.mCategories.get(0)));
        if (categories == null || categories.isEmpty())
            invitationItemView.mEventColor.setVisibility(View.GONE);
        else
            invitationItemView.mEventColor.setBackgroundColor(categories.get(0).getColor());

        invitationItemView.setLocation(mInvitation.mLocation);
        invitationItemView.setTime(DateAppearanceUtils.getEventInvitationTime(mInvitation));

        invitationItemView.mCreatedAgo.setText(DateUtils.getRelativeTimeSpanString(
                mInvitation.mChanged.getTime(), System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS));

        OtherUser user = db.getUserById(mInvitation.mInvitedBy);
        invitationItemView.mByWhoTitle.setText(user != null
                ? invitationItemView.getContext().getString(R.string.invited_by, user.mName)
                : "");
    }
}
