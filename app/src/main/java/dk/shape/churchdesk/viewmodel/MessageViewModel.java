package dk.shape.churchdesk.viewmodel;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Comment;
import dk.shape.churchdesk.entity.CommentObj;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.request.CreateCommentRequest;
import dk.shape.churchdesk.view.CommentView;
import dk.shape.churchdesk.view.MessageHeaderView;
import dk.shape.churchdesk.view.MessageView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class MessageViewModel extends ViewModel<MessageView> {

    public interface OnPostNewCommentListener {
        void onPost(CreateCommentRequest.CommentParameter parameter);
    }

    private final OnPostNewCommentListener listener;
    private final User mCurrentUser;

    private List<Comment> mComments = new ArrayList<>();
    private CommentsAdapter mAdapter;
    private Context mContext;
    private MessageView mMessageView;
    private CommentObj mMessage;

    public MessageViewModel(User currentUser, OnPostNewCommentListener listener) {
        this.mCurrentUser = currentUser;
        this.listener = listener;
    }

    @Override
    public void bind(MessageView messageView) {
        mMessageView = messageView;
        mContext = messageView.getContext();

        if (messageView.mCommentsView.getHeaderViewsCount() == 0) {
            MessageHeaderView view = new MessageHeaderView(messageView.getContext());
            MessageHeaderViewModel viewModel = new MessageHeaderViewModel(mCurrentUser, mMessage);
            messageView.mCommentsView.addHeaderView(view);
            viewModel.bind(view);
        }
        messageView.mCommentsView.setAdapter(mAdapter = new CommentsAdapter());
        messageView.mButtonReply.setOnClickListener(mOnReplyClick);
        messageView.mReply.addTextChangedListener(mEditTextWatcher);
    }

    private TextWatcher mEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int color = mContext.getResources().getColor(s == null || s.toString().isEmpty()
                    ? R.color.foreground_grey
                    : R.color.foreground_blue);
            mMessageView.mButtonReply.setTextColor(color);
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    private View.OnClickListener mOnReplyClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String reply = mMessageView.mReply.getText().toString();
            if (!reply.isEmpty()) {
                listener.onPost(new CreateCommentRequest.CommentParameter(
                        mMessage.mSiteUrl, reply, mMessage.id));

            }
        }
    };

    public void extBind(MessageView messageView, CommentObj commentObj) {
        Collections.reverse(commentObj.mComments);
        this.mComments = commentObj.mComments;
        this.mMessage = commentObj;
        bind(messageView);
    }

    public void addNewComment(Comment comment) {
        this.mComments.add(comment);
        this.mAdapter.notifyDataSetChanged();
        this.mMessageView.mReply.setText("");
        this.mMessageView.mCommentsView.smoothScrollToPosition(mComments.size() + 1);
    }

    private class CommentsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mComments.size();
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
            CommentView view = new CommentView(mContext);
            CommentViewModel viewModel = new CommentViewModel(mComments.get(position));
            viewModel.bind(view);
            return view;
        }
    }
}
