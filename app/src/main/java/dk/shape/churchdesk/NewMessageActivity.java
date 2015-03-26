package dk.shape.churchdesk;

import android.view.Menu;
import android.view.MenuItem;

import butterknife.InjectView;
import dk.shape.churchdesk.view.NewMessageView;
import dk.shape.churchdesk.viewmodel.NewMessageViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class NewMessageActivity extends BaseLoggedInActivity {

    private MenuItem mMenuSend;

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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private NewMessageViewModel.SendOkayListener mSendOkayListener =
            new NewMessageViewModel.SendOkayListener() {
                @Override
                public void okay(boolean isOkay) {
                    mMenuSend.setEnabled(isOkay);
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
}
