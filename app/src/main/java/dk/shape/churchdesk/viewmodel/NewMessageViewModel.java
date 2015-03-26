package dk.shape.churchdesk.viewmodel;

import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.view.NewMessageView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class NewMessageViewModel extends ViewModel<NewMessageView> {

    public interface SendOkayListener {
        void okay(boolean isOkay);
    }

    private final SendOkayListener mSendOkayListener;
    private final User mCurrentUser;

    public NewMessageViewModel(User currentUser, SendOkayListener listener) {
        this.mCurrentUser = currentUser;
        this.mSendOkayListener = listener;
    }

    @Override
    public void bind(NewMessageView newMessageView) {

    }
}
