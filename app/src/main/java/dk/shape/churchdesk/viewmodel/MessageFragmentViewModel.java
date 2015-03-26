package dk.shape.churchdesk.viewmodel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.view.MessageFragmentView;
import dk.shape.churchdesk.view.MessageItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class MessageFragmentViewModel extends ViewModel<MessageFragmentView> {

    private final User mCurrentUser;

    private List<Message> mMessages;
    private Context mContext;

    public MessageFragmentViewModel(User currentUser) {
        this.mCurrentUser = currentUser;
    }

    public void setData(List<Message> messages) {
        this.mMessages = messages;
    }

    @Override
    public void bind(MessageFragmentView messageFragmentView) {
        mContext = messageFragmentView.getContext();
        messageFragmentView.mMessageList.setAdapter(new MessageAdapter());
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
                    mMessages.get(position), mCurrentUser);
            viewModel.bind(view);
            return view;
        }
    }
}
