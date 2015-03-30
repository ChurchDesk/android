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
import dk.shape.churchdesk.view.MessageFragmentView;
import dk.shape.churchdesk.view.MessageItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class MessageFragmentViewModel extends ViewModel<MessageFragmentView> {


    public interface OnRefreshData {
        void onRefresh();
    }

    private final User mCurrentUser;
    private final OnRefreshData mOnRefreshData;
    private final MessageItemViewModel.OnMessageClickListener mOnMessageClickListener;

    private List<Message> mMessages;
    private Context mContext;

    public MessageFragmentViewModel(User currentUser, OnRefreshData onRefreshData,
                                    MessageItemViewModel.OnMessageClickListener onMessageClickListener) {
        this.mCurrentUser = currentUser;
        this.mOnRefreshData = onRefreshData;
        this.mOnMessageClickListener = onMessageClickListener;
    }

    public void setData(List<Message> messages) {
        this.mMessages = messages;
    }

    @Override
    public void bind(MessageFragmentView messageFragmentView) {
        mContext = messageFragmentView.getContext();
        messageFragmentView.swipeContainer.setRefreshing(false);
        messageFragmentView.swipeContainer.setColorSchemeResources(R.color.foreground_blue);
        messageFragmentView.swipeContainer.setOnRefreshListener(mOnRefreshListener);
        messageFragmentView.mMessageList.setAdapter(new MessageAdapter());
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mOnRefreshData.onRefresh();
        }
    };

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
