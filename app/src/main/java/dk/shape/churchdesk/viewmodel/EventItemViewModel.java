package dk.shape.churchdesk.viewmodel;

import android.view.View;

import java.util.List;

import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Category;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.util.DateAppearanceUtils;
import dk.shape.churchdesk.view.EventItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class EventItemViewModel extends ViewModel<EventItemView> {

    public interface OnEventClickListener {
        void onClick(Event event);
    }

    private final OnEventClickListener mListener;
    private final User mCurrentUser;
    private final Event mEvent;

    public EventItemViewModel(Event event, User currentUser,
                              OnEventClickListener listener) {
        this.mEvent = event;
        this.mCurrentUser = currentUser;
        this.mListener = listener;
    }

    @Override
    public void bind(EventItemView eventItemView) {
        DatabaseUtils db = DatabaseUtils.getInstance();

        Site site = null;
        if (!mCurrentUser.isSingleUser())
            site = mCurrentUser.getSiteById(mEvent.mSiteUrl);
        eventItemView.mEventSite.setText(site != null ? site.mSiteName : "");

        eventItemView.mEventTitle.setText(mEvent.mTitle);

        List<Category> categories = db.getCategoriesBySiteId(mEvent.mSiteUrl,
                String.valueOf(mEvent.mCategories.get(0)));
        if (categories == null || categories.isEmpty())
            eventItemView.mEventColor.setVisibility(View.GONE);
        else
            eventItemView.mEventColor.setBackgroundColor(categories.get(0).getColor());

        eventItemView.setLocation(mEvent.mLocation);

        eventItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(mEvent);
            }
        });

        eventItemView.mEventTime.setText(DateAppearanceUtils.getEventTime(
                eventItemView.getContext(), mEvent));
    }
}
