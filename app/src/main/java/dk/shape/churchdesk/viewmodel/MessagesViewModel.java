package dk.shape.churchdesk.viewmodel;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.view.MessageItemView;
import dk.shape.churchdesk.view.RefreshView;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class MessagesViewModel extends BaseDashboardViewModel<RefreshView, List<Message>> {

    private final User mCurrentUser;
    private final MessageItemViewModel.OnMessageClickListener mOnMessageClickListener;

    private List<Message> mMessages;
    private Context mContext;
    private boolean isDashboard;

    public MessagesViewModel(User currentUser, OnRefreshData onRefreshData,
                             MessageItemViewModel.OnMessageClickListener onMessageClickListener,
                             boolean isDashboard) {
        super(onRefreshData);
        this.mCurrentUser = currentUser;
        this.mOnMessageClickListener = onMessageClickListener;
        this.isDashboard = isDashboard;
    }

    @Override
    public void extBind(RefreshView view, List<Message> data) {
        this.mMessages = data;
        super.extBind(view, data);
    }

    @Override
    public int getEmptyRes() {
        return isDashboard ? R.string.no_unread : R.string.no_messages;
    }

    @Override
    public void bind(RefreshView refreshView) {
        mContext = refreshView.getContext();
        refreshView.swipeContainer.setRefreshing(false);
        refreshView.swipeContainer.setColorSchemeResources(R.color.foreground_blue);
        refreshView.swipeContainer.setOnRefreshListener(mOnRefreshListener);
        refreshView.mMessageList.setAdapter(new MessageAdapter());
    }

    private class MessageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMessages.size();
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
            MessageItemView view = new MessageItemView(mContext);
            MessageItemViewModel viewModel = new MessageItemViewModel(
                    mMessages.get(position), mCurrentUser, mOnMessageClickListener);
            viewModel.bind(view);
            return view;
        }
    }
}
