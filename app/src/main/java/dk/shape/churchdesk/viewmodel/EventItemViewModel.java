package dk.shape.churchdesk.viewmodel;

import android.view.View;

import java.util.Calendar;
import java.util.List;

import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Category;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.util.DateAppearanceUtils;
import dk.shape.churchdesk.view.EventItemView;
import dk.shape.library.collections.Categorizable;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class EventItemViewModel extends ViewModel<EventItemView> implements Categorizable {

    @Override
    public long getCategoryId() {
        return mEvent.mHeaderId;
    }

    public interface OnEventClickListener {
        void onClick(Event event);
    }

    private OnEventClickListener mListener;
    private User mCurrentUser;
    private Event mEvent;

    public EventItemViewModel(Event event, User currentUser,
                              OnEventClickListener listener) {
        this.mEvent = event;
        this.mCurrentUser = currentUser;
        this.mListener = listener;
    }

    public EventItemViewModel() {
    }

    public static EventItemViewModel instantiateAsDummy(Event dummyEvent) {
        EventItemViewModel viewModel = new EventItemViewModel();
        viewModel.mEvent = dummyEvent;
        return viewModel;
    }

    @Override
    public void bind(EventItemView eventItemView) {
        if (mEvent.isDummy) {
            eventItemView.mContentView.setVisibility(View.GONE);
            return;
        }
        eventItemView.mContentView.setVisibility(View.VISIBLE);
        DatabaseUtils db = DatabaseUtils.getInstance();

        Site site = null;
        if (!mCurrentUser.isSingleUser())
            site = mCurrentUser.getSiteById(mEvent.mSiteUrl);
        eventItemView.mEventSite.setText(site != null ? site.mSiteName : "");

        eventItemView.mEventTitle.setText(mEvent.mTitle);

        List<Category> categories = db.getCategoryBySiteId(mEvent.mSiteUrl,
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

        Calendar eventCal = Calendar.getInstance();
        eventCal.setTime(mEvent.mStartDate);

        eventItemView.mEventTime.setText(DateAppearanceUtils.getEventTime(
                eventItemView.getContext(), mEvent).toString());
    }
}
