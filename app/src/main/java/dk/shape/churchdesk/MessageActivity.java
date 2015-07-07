package dk.shape.churchdesk;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.Comment;
import dk.shape.churchdesk.entity.CommentObj;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.CreateCommentRequest;
import dk.shape.churchdesk.request.DeleteCommentRequest;
import dk.shape.churchdesk.request.GetMessageCommentsRequest;
import dk.shape.churchdesk.request.UpdateCommentRequest;
import dk.shape.churchdesk.view.MessageView;
import dk.shape.churchdesk.viewmodel.MessageViewModel;

/**
 * Created by steffenkarlsson on 30/03/15.
 */
public class MessageActivity extends BaseLoggedInActivity {

    private enum RequestTypes {
        COMMENTS,
        NEW_COMMENT,
        UPDATE_COMMENT,
        DELETE_COMMENT
    }

    public static final String KEY_MESSAGE = "KEY_MESSAGE";
    public static final String KEY_TYPE = "type";

    @InjectView(R.id.content_view)
    protected MessageView mContentView;

    private MessageViewModel mViewModel;

    private int mMessageId;
    private String mSiteUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_MESSAGE)) {
                Message _message = Parcels.unwrap(extras.getParcelable(KEY_MESSAGE));
                mMessageId = _message.id;
                mSiteUrl = _message.mSiteUrl;
                return;
            } else if (extras.containsKey(KEY_TYPE)
                    && extras.getString(KEY_TYPE, "").equalsIgnoreCase("message")) {
                mMessageId = Integer.valueOf(extras.getString("id", ""));
                mSiteUrl = extras.getString("site", "");
                return;
            }
        }
        finish();
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();
        this.showProgressDialog("Loading message", false);
        new GetMessageCommentsRequest(mMessageId, mSiteUrl)
                .withContext(this)
                .setOnRequestListener(listener)
                .run(RequestTypes.COMMENTS);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_message;
    }

    @Override
    protected int getTitleResource() {
        return R.string.message_title;
    }

    @Override
    protected boolean showBackButton() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
            dismissProgressDialog();
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK || result.statusCode == HttpStatus.SC_CREATED || result.statusCode == HttpStatus.SC_NO_CONTENT && result.response != null) {
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case COMMENTS:
                        CommentObj commentObj = (CommentObj) result.response;
                        mViewModel = new MessageViewModel(_user, new MessageViewModel.MessageViewModelListener() {
                            @Override
                            public void onPost(CreateCommentRequest.CommentParameter parameter) {
                                new CreateCommentRequest(parameter)
                                        .shouldReturnData()
                                        .withContext(MessageActivity.this)
                                        .setOnRequestListener(listener)
                                        .run(RequestTypes.NEW_COMMENT);
                            }

                            @Override
                            public void onCommentEdited(String siteId, Comment comment) {
                                new UpdateCommentRequest(comment, siteId)
                                        .shouldReturnData()
                                        .withContext(MessageActivity.this)
                                        .setOnRequestListener(listener)
                                        .run(RequestTypes.UPDATE_COMMENT);
                            }

                            @Override
                            public void onCommentDeleted(String siteId, Comment comment) {
                                new DeleteCommentRequest(siteId, comment)
                                        .withContext(MessageActivity.this)
                                        .setOnRequestListener(listener)
                                        .run(RequestTypes.DELETE_COMMENT);
                            }
                        });
                        mViewModel.extBind(mContentView, commentObj);
                        break;
                    case NEW_COMMENT:
                        Comment comment = (Comment) result.response;
                        comment.mAuthorId = _user.getSiteById(comment.mSiteUrl).mUserId;
                        comment.mAuthorName = _user.mName;
                        mViewModel.addNewComment(comment);
                        break;
                    case UPDATE_COMMENT:
                        mViewModel.commentUpdated();
                        break;
                    case DELETE_COMMENT:
                        mViewModel.commentDeleted();
                        break;
                }

             }
            dismissProgressDialog();
        }

        @Override
        public void onProcessing() {
        }
    };
}
