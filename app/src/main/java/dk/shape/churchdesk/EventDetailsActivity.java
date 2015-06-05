package dk.shape.churchdesk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetSingleEventRequest;
import dk.shape.churchdesk.view.EventDetailsView;
import dk.shape.churchdesk.viewmodel.EventDetailsViewModel;

/**
 * Created by Martin on 02/06/2015.
 */
public class EventDetailsActivity extends BaseLoggedInActivity {

    private MenuItem mMenuEditEvent;

    private Event _event;

    public static final String KEY_EVENT = "KEY_EVENT";


    @InjectView(R.id.content_view)
    protected EventDetailsView mContentView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_edit, menu);
        mMenuEditEvent = menu.findItem(R.id.menu_event_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_edit:
                //TODO: Link to edit event
                Bundle bundle = new Bundle();
                bundle.putParcelable(NewEventActivity.KEY_EVENT_EDIT, Parcels.wrap(_event));
                Intent i = this.getActivityIntent(this, NewEventActivity.class, bundle);
                startActivity(i);
                Log.d("ERRORERROR", "onClickEditEvent");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.containsKey(KEY_EVENT)) {
                _event = Parcels.unwrap(extras.getParcelable(KEY_EVENT));
                if(!_event.canEdit){
                    mMenuEditEvent.setVisible(false);
                }
                return;
            }
        }
        finish();
    }

    @Override
    protected void onUserAvailable() {
        new GetSingleEventRequest(_event.getId(), _event.mSiteUrl)
                .withContext(this)
                .setOnRequestListener(listener)
                .run();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_event_details;
    }

    @Override
    protected int getTitleResource() {
        return R.string.event_details_title;
    }


    @Override
    protected boolean showBackButton() {
        return true;
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {

        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    && result.response != null) {
                _event = (Event)result.response;
                EventDetailsViewModel viewModel = new EventDetailsViewModel(_user, _event);
                viewModel.bind(mContentView);
            }
        }

        @Override
        public void onProcessing() {
        }
    };

}
