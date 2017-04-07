package dk.shape.churchdesk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import java.util.List;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.entity.Segment;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.HttpStatusCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.CreateMessageRequest;
import dk.shape.churchdesk.request.CreatePeopleMessageRequest;
import dk.shape.churchdesk.request.GetPeople;
import dk.shape.churchdesk.request.GetSegments;
import dk.shape.churchdesk.view.NewMessageView;
import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.churchdesk.viewmodel.NewMessageViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class NewMessageActivity extends BaseLoggedInActivity {

    private MenuItem mMenuSend;
    private CreateMessageRequest.MessageParameter mParameter;
    private CreatePeopleMessageRequest.MessageParameter mPeopleMessageParameter;
    private String mMessageType;
    private NewMessageViewModel viewModel;
    @InjectView(R.id.content_view)
    protected NewMessageView mContentView;

    private enum RequestTypes {
        PEOPLE, SEGMENTS, NEW_MESSAGE
    }
    @Override
    protected void onUserAvailable() {
        mMessageType = getIntent().getStringExtra("EXTRA_MESSAGE_TYPE");
        super.onUserAvailable();

        //set appropriate title for people message
        if (mMessageType.equals("email"))
            NewMessageActivity.this.setActionBarTitle(R.string.new_people_email_title);
        else if (mMessageType.equals("sms"))
            NewMessageActivity.this.setActionBarTitle(R.string.new_people_sms_title);
        if (!mMessageType.equals("message")){
            viewModel = new NewMessageViewModel(_user, null, mSendPeopleMessageOkayListener);
            viewModel.mMessageType = mMessageType;
            Integer selectedPersonId = getIntent().getIntExtra("personId", 0);
            if (selectedPersonId != 0) {
                viewModel.isPeople = true;
                viewModel.mSelectedPeople.add(selectedPersonId);
            }
            viewModel.bind(mContentView);
            loadPeople();
            loadSegments();
        }
        else {
            viewModel = new NewMessageViewModel(_user, mSendOkayListener, null);
            viewModel.mMessageType = mMessageType;
            viewModel.bind(mContentView);
        }

    }

    private void loadPeople() {
        new GetPeople(viewModel.mSelectedOrganizationId, 0).withContext(this)
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.PEOPLE);
    }

    private void loadSegments() {
        new GetSegments(viewModel.mSelectedOrganizationId).withContext(this)
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.SEGMENTS);
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
                if (mParameter != null && mMessageType.equals("message")) {
                    mMenuSend.setEnabled(false);
                    showProgressDialog(R.string.new_message_create_progress, false);
                        new CreateMessageRequest(mParameter)
                            .withContext(this)
                            .setOnRequestListener(listener)
                            .run(RequestTypes.NEW_MESSAGE);

                } else if (mPeopleMessageParameter != null && !mMessageType.equals("message"))
                {
                    mMenuSend.setEnabled(false);
                    showProgressDialog(R.string.new_message_create_progress, false);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NewMessageActivity.this);
                    String selectedOrganizationId = prefs.getString("selectedOrgaziationIdForPeople", "");
                    mPeopleMessageParameter.mOrganizationId = selectedOrganizationId;
                    mPeopleMessageParameter.mType = mMessageType;
                    new CreatePeopleMessageRequest(mPeopleMessageParameter, selectedOrganizationId)
                            .withContext(this)
                            .setOnRequestListener(listener)
                            .run(RequestTypes.NEW_MESSAGE);
                    Toast.makeText(getApplicationContext(), "Message was send", Toast.LENGTH_LONG).show();
                }
                {
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
                    try {
                        mMenuSend.setEnabled(isOkay);
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    if (isOkay)
                        mParameter = parameter;
                }
            };

    private NewMessageViewModel.SendPeopleMessageOkayListener mSendPeopleMessageOkayListener =
            new NewMessageViewModel.SendPeopleMessageOkayListener() {
                @Override
                public void okay(boolean isOkay, CreatePeopleMessageRequest.MessageParameter parameter) {
                    try {
                        mMenuSend.setEnabled(isOkay);
                    } catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    if (isOkay)
                        mPeopleMessageParameter = parameter;
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
            if (result.statusCode == HttpStatusCode.SC_OK && result.response != null) {
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case PEOPLE: {
                        viewModel.mPeopleList = (List<Person>) result.response;
                        break;
                    }
                    case SEGMENTS: {
                         viewModel.mSegmentsList = (List<Segment>) result.response;
                        break;
                    }
                }
            }
            else if (result.statusCode == HttpStatusCode.SC_CREATED){
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NewMessageActivity.this);
                prefs.edit().putBoolean("newMessage", true).commit();
                dismissProgressDialog();
                finish();
            }
        }

        @Override
        public void onProcessing() { }
    };
}
