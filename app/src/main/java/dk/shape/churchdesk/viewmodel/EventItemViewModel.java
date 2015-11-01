package dk.shape.churchdesk.viewmodel;

import android.view.View;

import java.util.Calendar;

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

    public boolean isDummy() {
        return mEvent.isDummy;
    }

    public interface OnEventClickListener {
        void onClick(Event event);
    }

    private OnEventClickListener mListener;
    private User mCurrentUser;
    private Event mEvent;
    private boolean isCalendar;

    public EventItemViewModel(Event event, User currentUser,
                              OnEventClickListener listener,
                              boolean isCalendar) {
        this.mEvent = event;
        this.mCurrentUser = currentUser;
        this.mListener = listener;
        this.isCalendar = isCalendar;
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
        eventItemView.mDivider.setVisibility(isCalendar ? View.VISIBLE : View.GONE);

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

        Category mainCategory = mEvent.getMainCategory();
        if (mainCategory == null) {
            eventItemView.mEventColor.setVisibility(View.GONE);
        } else {
            eventItemView.mEventColor.setBackgroundColor(mainCategory.getColor());
        }

        //List<Category> categories = db.getCategoryBySiteId(mEvent.mSiteUrl, String.valueOf(mEvent.mCategories.get(0)));
//        if (categories == null || categories.isEmpty())
//            eventItemView.mEventColor.setVisibility(View.GONE);
//        else
//            eventItemView.mEventColor.setBackgroundColor(categories.get(0).getColor());

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

    @Override
    public boolean equals(Object o) {
        return (o instanceof EventItemViewModel) &&
                mEvent.mHeaderId == ((EventItemViewModel)o).mEvent.mHeaderId
                || (o instanceof Event) && mEvent.mHeaderId == ((Event)o).mHeaderId
                || (o instanceof Calendar) && mEvent.mHeaderId == ((Calendar)o).getTimeInMillis();
    }

    public boolean compareOnId(Object o) {
        if (o instanceof EventItemViewModel) {
            Integer id = mEvent.getId();
            Integer id2 = ((EventItemViewModel)o).mEvent.getId();
            if (id != null && id2 != null)
                return id.equals(id2);
        }
        return false;
    }

    public boolean before(EventItemViewModel o) {
        return mEvent.mHeaderId < o.mEvent.mHeaderId;
    }

    public boolean before(Calendar o) {
        return mEvent.mHeaderId < o.getTimeInMillis();
    }

    public boolean after(Calendar o) {
        return mEvent.mHeaderId > o.getTimeInMillis();
    }

    public boolean after(EventItemViewModel o) {
        return mEvent.mHeaderId > o.mEvent.mHeaderId;
    }

    public boolean isMyEvent() {
        return mEvent.isMyEvent(mCurrentUser);
    }
}
