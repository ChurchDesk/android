package dk.shape.churchdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.entity.Tag;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.CreatePersonRequest;
import dk.shape.churchdesk.request.EditPersonRequest;
import dk.shape.churchdesk.request.GetPeople;
import dk.shape.churchdesk.request.GetTags;
import dk.shape.churchdesk.util.Validators;
import dk.shape.churchdesk.view.NewPersonView;
import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.churchdesk.viewmodel.NewPersonViewModel;

/**
 * Created by chirag on 22/02/2017.
 */
public class NewPersonActivity extends BaseLoggedInActivity {

    private MenuItem mMenuCreatePerosn;
    private MenuItem mMenuSavePerson;
    private String selectedOrganizationId;
    private CreatePersonRequest.PersonParameter mPersonParameter;
    private NewPersonViewModel viewModel;
    public static String KEY_PERSON_EDIT = "KEY_EDIT_Person";
    Person _person;

    private enum RequestTypes {
        TAGS, CREATE_PERSON, EDIT_PERSON
    }

    @InjectView(R.id.content_view)
    protected NewPersonView mContentView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_add, menu);
        mMenuCreatePerosn = menu.findItem(R.id.menu_event_add);
        mMenuSavePerson = menu.findItem(R.id.menu_event_save);
        setEnabled(mMenuCreatePerosn, false);
        if (_person != null) {
            mMenuCreatePerosn.setVisible(false);
            mMenuSavePerson.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_add:
                createNewPerson();
                return true;
            case R.id.menu_event_save:
                editPerson();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewPerson() {
        if (mPersonParameter != null) {
            if ( mPersonParameter.mEmail != null && !mPersonParameter.mEmail.isEmpty() && !Validators.isValidEmail(mContentView.mPersonEmailChosen)){
                mContentView.mPersonEmailChosen.setError(getString(R.string.login_email_validation_error));
            }
            else {
                savePerson();
            }
        }
        Log.d("Person 1", "onClickAddPerson");
    }

    private void savePerson(){
        new CreatePersonRequest(mPersonParameter, selectedOrganizationId)
                .withContext(this)
                .setOnRequestListener(listener)
                .run(RequestTypes.CREATE_PERSON);
        setEnabled(mMenuCreatePerosn, false);
        showProgressDialog(R.string.new_person_create_progress, false);
    }

    private void editPerson() {
        if (mPersonParameter != null) {
            if ( mPersonParameter.mEmail != null && !mPersonParameter.mEmail.isEmpty() && !Validators.isValidEmail(mContentView.mPersonEmailChosen)){
                mContentView.mPersonEmailChosen.setError(getString(R.string.login_email_validation_error));
            }
            else {
                new EditPersonRequest(_person.mPeopleId, selectedOrganizationId, mPersonParameter)
                        .withContext(this)
                        .setOnRequestListener(listener)
                        .run(RequestTypes.EDIT_PERSON);
                setEnabled(mMenuSavePerson, false);
                showProgressDialog(R.string.edit_person_edit_progress, false);
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        selectedOrganizationId = prefs.getString("selectedOrgaziationIdForPeople", "");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_PERSON_EDIT)) {
                _person = Parcels.unwrap(extras.getParcelable(KEY_PERSON_EDIT));
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();
        viewModel = new NewPersonViewModel(mSendOKListener);
        viewModel.bind(mContentView);
        if (_person != null) {
            viewModel.setDataToEdit(_person);
        }
        loadTags();

    }

    private void loadTags() {
        new GetTags(selectedOrganizationId).withContext(this)
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.TAGS);
    }

    private NewPersonViewModel.SendOkayListener mSendOKListener = new NewPersonViewModel.SendOkayListener() {
        @Override
        public void okay(boolean isOkay, CreatePersonRequest.PersonParameter parameter) {
            setEnabled(mMenuCreatePerosn, isOkay);
            setEnabled(mMenuSavePerson, isOkay);
            if (isOkay) {
                mPersonParameter = parameter;
            }
        }
    };

    private void setEnabled(MenuItem item, boolean enabled) {
        item.setEnabled(enabled);
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), _person == null ? R.string.new_person_create_error : R.string.edit_person_edit_error, Toast.LENGTH_SHORT).show();
                setEnabled(mMenuCreatePerosn, true);
        }

        @Override
        public void onSuccess(int id, Result result) {
            switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                case TAGS: {
                    viewModel.tags = (List<Tag>) result.response;
                    break;
                }
                default:{
                    if (result.statusCode == HttpStatus.SC_OK
                            || result.statusCode == HttpStatus.SC_CREATED
                            || result.statusCode == HttpStatus.SC_NO_CONTENT) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NewPersonActivity.this);
                        prefs.edit().putBoolean("newPerson", true).commit();
                        finish();
                    }
                    dismissProgressDialog();
                }
            }
        }

        @Override
        public void onProcessing() {
        }
    };


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_new_person;
    }

    @Override
    protected int getTitleResource() {
        return _person == null ? R.string.new_person_title : R.string.edit_person_title;
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

