package dk.shape.churchdesk;

import android.os.Bundle;

import org.parceler.Parcels;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.view.MessageView;
import dk.shape.churchdesk.viewmodel.MessageViewModel;

/**
 * Created by steffenkarlsson on 30/03/15.
 */
public class MessageActivity extends BaseLoggedInActivity {

    public static final String KEY_MESSAGE = "KEY_MESSAGE";

    private Message _message;

    @InjectView(R.id.content_view)
    protected MessageView mContentView;

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
        MessageViewModel viewModel = new MessageViewModel(_user, _message);
        viewModel.bind(mContentView);
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
}
