package dk.shape.churchdesk.viewmodel;

import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.view.MessageView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class MessageViewModel extends ViewModel<MessageView> {

    private final User mCurrentUser;
    private final Message mMessage;

    public MessageViewModel(User currentUser, Message message) {
        this.mCurrentUser = currentUser;
        this.mMessage = message;
    }

    @Override
    public void bind(MessageView messageView) {

    }
}
