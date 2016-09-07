package dk.shape.churchdesk.viewmodel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.adapter.DividerItemDecoration;
import dk.shape.churchdesk.entity.Comment;
import dk.shape.churchdesk.entity.CommentObj;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.request.CreateCommentRequest;
import dk.shape.churchdesk.view.MessageView;
import dk.shape.library.collections.adapters.RecyclerAdapter;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class MessageViewModel extends ViewModel<MessageView> implements SeeAllCommentsViewModel.SeeAllCommentsListener, CommentViewModel.CommentViewModelListener {

    public interface MessageViewModelListener {
        void onPost(CreateCommentRequest.CommentParameter parameter);
        void onCommentEdited(String siteId, Comment comment);
        void onCommentDeleted(String siteId, Comment comment);
    }

    private final MessageViewModelListener _listener;
    private final User mCurrentUser;

    private List<Comment> mComments = new ArrayList<>();
    private Context mContext;
    private MessageView mMessageView;
    private CommentObj mMessage;

    private RecyclerAdapter<ViewModel> _adapter;

    private List<ViewModel> _commentViewModels = new ArrayList<>();
    private DividerItemDecoration _itemDecoration;

    private Comment _commentCurrentlyBeingEdited;

    private InputMethodManager _inputManager;

    public MessageViewModel(User currentUser, MessageViewModelListener listener) {
        this.mCurrentUser = currentUser;
        this._listener = listener;
    }

    @Override
    public void bind(MessageView messageView) {

        _inputManager = (InputMethodManager)messageView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        _adapter = new RecyclerAdapter<>(messageView.getContext());

        _itemDecoration = new DividerItemDecoration(messageView.getResources().getDrawable(R.drawable.item_divider));

        messageView.mCommentsView.addItemDecoration(_itemDecoration);

        messageView.mCommentsView.setAdapter(_adapter);
        messageView.mCommentsView.setLayoutManager(new LinearLayoutManager(messageView.getContext()));

        mMessageView = messageView;
        mContext = messageView.getContext();

        MessageHeaderViewModel viewModel = new MessageHeaderViewModel(mCurrentUser, mMessage);
        _adapter.add(viewModel);

        final int commentsCount = mComments.size();

        if(commentsCount > 0) {
            for (Comment comment : mComments) {
                _commentViewModels.add(new CommentViewModel(this, comment));
            }

            if(commentsCount > 2) {
                int lastIndex = _commentViewModels.size() - 1;

                // create the "SeeAllViewModel" and add to adapter
                _adapter.add(new SeeAllCommentsViewModel(this, _commentViewModels.size() - 1));
                _adapter.add(_commentViewModels.get(lastIndex));

                _commentViewModels.remove(lastIndex);
            }
            else {
                for (ViewModel commentViewModel : _commentViewModels) {
                    _adapter.add(commentViewModel);
                }
            }
        }

        messageView.mButtonReply.setOnClickListener(mOnReplyClick);
        messageView.mReply.addTextChangedListener(mEditTextWatcher);
        mMessageView.mReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMessageView.mCommentsView.scrollToPosition(mComments.size());
            }
        });
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
                _inputManager.hideSoftInputFromWindow(mMessageView.mReply.getWindowToken(), 0);
                mMessageView.mReply.setText("");
                mMessageView.mReply.clearFocus();

                if(_commentCurrentlyBeingEdited == null) {
                    _listener.onPost(new CreateCommentRequest.CommentParameter(mMessage.mSiteUrl, reply, mMessage.id));
                }
                else {
                    Comment comment = _commentCurrentlyBeingEdited;
                    comment.mBody = reply;
                    _listener.onCommentEdited(mMessage.mSiteUrl, comment);
                }
            }
        }
    };

    public void extBind(MessageView messageView, CommentObj commentObj) {
        Collections.reverse(commentObj.mComments);
        this.mComments = commentObj.mComments;
        this.mMessage = commentObj;
        bind(messageView);
    }

    public void commentUpdated() {
        _commentCurrentlyBeingEdited = null;
        _adapter.notifyDataSetChanged();
    }

    public void commentDeleted() {
        CommentViewModel commentViewModel = findViewModel();
        if(commentViewModel != null) {
            _adapter.remove(commentViewModel);
        }
        _commentCurrentlyBeingEdited = null;

        _adapter.notifyDataSetChanged();
    }

    private CommentViewModel findViewModel() {
        for (int i = 0; i < _adapter.getItems().size(); i++) {
            ViewModel viewModel = _adapter.getItem(i);

            if(viewModel instanceof CommentViewModel) {
                CommentViewModel commentViewModel = (CommentViewModel)viewModel;
                if(commentViewModel.getComment().equals(_commentCurrentlyBeingEdited)) {
                    return commentViewModel;
                }
            }
        }

        return null;
    }

    public void addNewComment(Comment comment) {
        _adapter.add(new CommentViewModel(this, comment));
        _adapter.notifyItemInserted(_adapter.getItemCount() - 1);

        this.mMessageView.mCommentsView.smoothScrollToPosition(mComments.size() + 1);
    }

    @Override
    public void onSeeAllCommentsClicked() {
        _adapter.remove(_adapter.getItem(1));

        Collections.reverse(_commentViewModels);

        for (ViewModel commentViewModel : _commentViewModels) {
            _adapter.add(1, commentViewModel);
        }

        Collections.reverse(_commentViewModels);

        _commentViewModels.clear();

        _adapter.notifyItemRangeChanged(1, _adapter.getItemCount());
    }

    @Override
    public void onEditClicked(Comment comment) {
        _commentCurrentlyBeingEdited = comment;

        mMessageView.mReply.setText(comment.mBody);
        mMessageView.mReply.setSelection(mMessageView.mReply.length());
        mMessageView.mCommentsView.scrollToPosition(mComments.indexOf(comment));
    }

    @Override
    public void onDeleteClicked(Comment comment) {
        displayDeleteDialog(comment);
    }


    private void displayDeleteDialog(final Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMessageView.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle(R.string.comment_dialog_title);
        builder.setMessage(R.string.comment_dialog_message);
        builder.setPositiveButton(R.string.comment_dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _commentCurrentlyBeingEdited = comment;

                dialog.dismiss();
                _listener.onCommentDeleted(mMessage.mSiteUrl, comment);
            }
        });
        builder.setNegativeButton(R.string.comment_dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}