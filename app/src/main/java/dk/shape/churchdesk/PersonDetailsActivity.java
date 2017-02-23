package dk.shape.churchdesk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetSinglePersonRequest;
import dk.shape.churchdesk.view.PersonDetailsView;
import dk.shape.churchdesk.viewmodel.PersonDetailsViewModel;

/**
 * Created by chirag on 23/02/2017.
 */
public class PersonDetailsActivity extends BaseLoggedInActivity {

    private MenuItem mMenuEditPerson;

    private int mPersonId;
    private String mOrganizationId;
    private Person _person;

    public static final String KEY_PERSON = "KEY_PERSON";

    @InjectView(R.id.content_view)
    protected PersonDetailsView mContentView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_edit, menu);
        mMenuEditPerson = menu.findItem(R.id.menu_event_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_edit:
                //TODO: Link to edit event
                Bundle bundle = new Bundle();
                    bundle.putParcelable(NewPersonActivity.KEY_PERSON_EDIT, Parcels.wrap(_person));
                    Intent i = this.getActivityIntent(this, NewPersonActivity.class, bundle);
                    startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        _person = Parcels.unwrap(extras.getParcelable(KEY_PERSON));
        mPersonId = _person.mPeopleId;
        mOrganizationId = "58";
        return;
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();
        showProgressDialog("Loading information", true);
        new GetSinglePersonRequest(mPersonId, mOrganizationId)
                .withContext(this)
                .setOnRequestListener(listener)
                .run();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_person_details;
    }

    @Override
    protected int getTitleResource() {
            return R.string.person_details_title;
    }

    @Override
    protected boolean showBackButton() {
        return true;
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
            dismissProgressDialog();
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    && result.response != null) {
                _person = (Person) result.response;
                mMenuEditPerson.setVisible(true);

                PersonDetailsViewModel viewModel = new PersonDetailsViewModel(_user, _person);
                viewModel.bind(mContentView);
            }
            dismissProgressDialog();
        }
        @Override
        public void onProcessing() {
        }
    };
}
