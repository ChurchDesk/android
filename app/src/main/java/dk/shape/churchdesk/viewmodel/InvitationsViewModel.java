package dk.shape.churchdesk.viewmodel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.view.InvitationItemView;
import dk.shape.churchdesk.view.RefreshLoadMoreView;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class InvitationsViewModel extends BaseDashboardViewModel<RefreshLoadMoreView, List<Event>> {

    private final User mCurrentUser;

    private List<Event> mEventInvites;
    private Context mContext;

    public InvitationsViewModel(User currentUser, OnRefreshData onRefreshData) {
        super(onRefreshData);
        this.mCurrentUser = currentUser;
    }

    @Override
    public void extBind(RefreshLoadMoreView view, List<Event> data) {
        this.mEventInvites = data;
        super.extBind(view, data);
    }

    @Override
    public int getEmptyRes() {
        return R.string.no_invitations;
    }

    @Override
    public void bind(RefreshLoadMoreView refreshView) {
        mContext = refreshView.getContext();
        refreshView.swipeContainer.setRefreshing(false);
        refreshView.swipeContainer.setColorSchemeResources(R.color.foreground_blue);
        refreshView.swipeContainer.setOnRefreshListener(mOnRefreshListener);
        refreshView.mDataList.setAdapter(new InvitesAdapter());
    }

    private class InvitesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mEventInvites.size();
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
            InvitationItemView view = new InvitationItemView(mContext);
            InvitationItemViewModel viewModel = new InvitationItemViewModel(
                    mEventInvites.get(position), mCurrentUser);
            viewModel.bind(view);
            return view;
        }
    }
}
