package dk.shape.churchdesk;

import android.os.Bundle;
import android.view.MenuItem;

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
import dk.shape.churchdesk.request.GetMessageCommentsRequest;
import dk.shape.churchdesk.view.MessageView;
import dk.shape.churchdesk.viewmodel.MessageViewModel;

/**
 * Created by steffenkarlsson on 30/03/15.
 */
public class MessageActivity extends BaseLoggedInActivity {

    private enum RequestTypes {
        COMMENTS, NEW_COMMENT
    }

    public static final String KEY_MESSAGE = "KEY_MESSAGE";

    private Message _message;

    @InjectView(R.id.content_view)
    protected MessageView mContentView;

    private MessageViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_MESSAGE)) {
                _message = Parcels.unwrap(extras.getParcelable(KEY_MESSAGE));
                return;
            }
        }
        finish();
    }

    @Override
    protected void onUserAvailable() {
        new GetMessageCommentsRequest(_message.id, _message.mSiteUrl)
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

        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    || result.statusCode == HttpStatus.SC_CREATED
                    && result.response != null) {
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case COMMENTS:
                        CommentObj commentObj = (CommentObj) result.response;
                        mViewModel = new MessageViewModel(_user, new MessageViewModel.OnPostNewCommentListener() {
                            @Override
                            public void onPost(CreateCommentRequest.CommentParameter parameter) {
                                new CreateCommentRequest(parameter)
                                        .shouldReturnData()
                                        .withContext(MessageActivity.this)
                                        .setOnRequestListener(listener)
                                        .run(RequestTypes.NEW_COMMENT);
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
                }

             }
        }

        @Override
        public void onProcessing() {
        }
    };
}
