package dk.shape.churchdesk.viewmodel;

import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.view.EventDetailsView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by Martin on 02/06/2015.
 */
public class EventDetailsViewModel extends ViewModel<EventDetailsView> {

    public EventDetailsViewModel(User mCurrentUser, int eventId) {
        System.out.println(eventId);
    }



    @Override
    public void bind(EventDetailsView eventDetailsView) {

    }



}
