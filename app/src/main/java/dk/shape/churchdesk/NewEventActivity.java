package dk.shape.churchdesk;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.HttpStatus;

import butterknife.InjectView;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.CreateEventRequest;
import dk.shape.churchdesk.view.NewEventView;
import dk.shape.churchdesk.viewmodel.NewEventViewModel;

/**
 * Created by Martin on 20/05/2015.
 */
public class NewEventActivity extends BaseLoggedInActivity{

    private MenuItem mMenuCreateEvent;
    private CreateEventRequest.EventParameter mEventParameter;


    @InjectView(R.id.content_view)
    protected NewEventView mContentView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_add, menu);
        mMenuCreateEvent = menu.findItem(R.id.menu_event_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_add:
                if(mEventParameter != null){
                    new CreateEventRequest(mEventParameter)
                            .withContext(this)
                            .setOnRequestListener(listener)
                            .run();
                    mMenuCreateEvent.setEnabled(false);
                    //TODO show loading dialog or something
                } else {
                    //TODO: Error
                }
                Log.d("ERRORERROR", "onClickAddEvent");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
         protected void onUserAvailable() {
        NewEventViewModel viewModel = new NewEventViewModel(_user, mSendOKListener);
        viewModel.bind(mContentView);
    }

    private NewEventViewModel.SendOkayListener mSendOKListener = new NewEventViewModel.SendOkayListener() {
        @Override
        public void okay(boolean isOkay, CreateEventRequest.EventParameter parameter) {
            mMenuCreateEvent.setEnabled(isOkay);
            if(isOkay){
                mEventParameter = parameter;
            }

        }
    };

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
            //TODO: Error
            Toast.makeText(getApplicationContext(), "Error creating the event", Toast.LENGTH_SHORT).show();
            mMenuCreateEvent.setEnabled(true);
        }

        @Override
        public void onSuccess(int id, Result result) {
            if ((result.statusCode == HttpStatus.SC_OK
                    || result.statusCode == HttpStatus.SC_CREATED)
                    && result.response != null) {
                finish();
                //TODO: end loading dialog or something
            }
        }

        @Override
        public void onProcessing() { }
    };


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_new_event;
    }

    @Override
    protected int getTitleResource() {
        return R.string.new_event_title;
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
