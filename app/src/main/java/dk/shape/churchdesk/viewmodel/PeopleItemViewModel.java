package dk.shape.churchdesk.viewmodel;

import android.graphics.Color;
import android.view.View;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.MessageItemView;
import dk.shape.churchdesk.view.PersonItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by chirag on 13/02/2017.
 */
public class PeopleItemViewModel extends ViewModel<PersonItemView> {
    public interface OnPersonClickListener {
        void onClick(Person person);
    }

    private final OnPersonClickListener mListener;
    private final Person mPerson;

    public PeopleItemViewModel(Person person,
                                OnPersonClickListener listener) {
        this.mPerson = person;
        this.mListener = listener;
    }

    @Override
    public void bind(PersonItemView personItemView) {
        //to display elements in person cell
        personItemView.mDivider.setVisibility(View.VISIBLE);
        personItemView.mContentView.setVisibility(View.VISIBLE);
        if (mPerson.mFullName.equals("unknown"))
            personItemView.mName.setText(R.string.unknown);
        else
            personItemView.mName.setText(mPerson.mFullName);
        personItemView.mName.setTextColor(Color.rgb(0, 0, 0));
        personItemView.mAbsenceIcon.setVisibility(View.GONE);
        personItemView.setEmail(mPerson.mEmail);
        personItemView.mPersonColor.setVisibility(View.INVISIBLE);
        personItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(mPerson);
            }
        });
    }
}
