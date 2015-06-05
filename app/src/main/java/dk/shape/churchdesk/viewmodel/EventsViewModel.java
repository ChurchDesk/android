package dk.shape.churchdesk.viewmodel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.view.EventItemView;
import dk.shape.churchdesk.view.RefreshLoadMoreView;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class EventsViewModel extends BaseDashboardViewModel<RefreshLoadMoreView, List<Event>> {

    private final User mCurrentUser;
    private final EventItemViewModel.OnEventClickListener mOnEventClickListener;

    private List<Event> mEvents;
    private Context mContext;

    public EventsViewModel(User currentUser, OnRefreshData onRefreshData,
                           EventItemViewModel.OnEventClickListener onEventClickListener) {
        super(onRefreshData);
        this.mCurrentUser = currentUser;
        this.mOnEventClickListener = onEventClickListener;
    }

    @Override
    public void extBind(RefreshLoadMoreView view, List<Event> data) {
        this.mEvents = data;
        super.extBind(view, data);
    }

    @Override
    public int getEmptyRes() {
        return R.string.no_events;
    }

    @Override
    public void bind(RefreshLoadMoreView refreshView) {
        mContext = refreshView.getContext();
        refreshView.swipeContainer.setRefreshing(false);
        refreshView.swipeContainer.setColorSchemeResources(R.color.foreground_blue);
        refreshView.swipeContainer.setOnRefreshListener(mOnRefreshListener);
        refreshView.mDataList.setAdapter(new EventsAdapter());
    }

    private class EventsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mEvents.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EventItemView view = new EventItemView(mContext);
            EventItemViewModel viewModel = new EventItemViewModel(
                    mEvents.get(position), mCurrentUser, mOnEventClickListener, false);
            viewModel.bind(view);
            return view;
        }
    }
}
