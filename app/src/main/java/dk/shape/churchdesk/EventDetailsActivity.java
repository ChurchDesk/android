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

    private int mEventId;
    private String mSiteUrl;
    private Event _event;

    public static final String KEY_EVENT = "KEY_EVENT";
    public static final String KEY_TYPE = "type";

    @InjectView(R.id.content_view)
    protected EventDetailsView mContentView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_edit, menu);
        mMenuEditEvent = menu.findItem(R.id.menu_event_edit);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(_event != null && !_event.canEdit && mMenuEditEvent != null){
            mMenuEditEvent.setVisible(false);
            mMenuEditEvent.setEnabled(false);
        }
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
                mEventId = _event.getId();
                mSiteUrl = _event.mSiteUrl;
                return;
            } else if (extras.containsKey(KEY_TYPE)
                    && !extras.getString(KEY_TYPE, "").equalsIgnoreCase("message")) {
                mEventId = Integer.valueOf(extras.getString("id", ""));
                mSiteUrl = extras.getString("site", "");
                return;
            }
        }
        finish();
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();
        showProgressDialog("Loading information", true);
        new GetSingleEventRequest(mEventId, mSiteUrl)
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
            dismissProgressDialog();
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    && result.response != null) {
                _event = (Event)result.response;
                EventDetailsViewModel viewModel = new EventDetailsViewModel(_user, _event);
                viewModel.bind(mContentView);
            }
            if (_event.mPicture == null || _event.mPicture.isEmpty()) {
                dismissProgressDialog();
            }
        }

        @Override
        public void onProcessing() {
        }
    };

}
