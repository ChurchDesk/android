package dk.shape.churchdesk;

import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpStatus;

import butterknife.InjectView;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.CreateMessageRequest;
import dk.shape.churchdesk.view.NewMessageView;
import dk.shape.churchdesk.viewmodel.NewMessageViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class NewMessageActivity extends BaseLoggedInActivity {

    private MenuItem mMenuSend;
    private CreateMessageRequest.MessageParameter mParameter;

    @InjectView(R.id.content_view)
    protected NewMessageView mContentView;

    @Override
    protected void onUserAvailable() {
        NewMessageViewModel viewModel = new NewMessageViewModel(_user, mSendOkayListener);
        viewModel.bind(mContentView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        mMenuSend = menu.findItem(R.id.menu_send);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_send:
                if (mParameter != null) {
                    mMenuSend.setEnabled(false);
                    showProgressDialog(R.string.new_message_create_progress, false);
                    new CreateMessageRequest(mParameter)
                            .withContext(this)
                            .setOnRequestListener(listener)
                            .run();
                } else {
                    //TODO: Error
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private NewMessageViewModel.SendOkayListener mSendOkayListener =
            new NewMessageViewModel.SendOkayListener() {
                @Override
                public void okay(boolean isOkay, CreateMessageRequest.MessageParameter parameter) {
                    mMenuSend.setEnabled(isOkay);
                    if (isOkay)
                        mParameter = parameter;
                }
            };

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_new_message;
    }

    @Override
    protected int getTitleResource() {
        return R.string.new_message_title;
    }

    @Override
    protected boolean showCancelButton() {
        return true;
    }

    @Override
    protected boolean showBackButton() {
        return false;
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_CREATED) {
                dismissProgressDialog();
                finish();
            }
        }

        @Override
        public void onProcessing() { }
    };
}
