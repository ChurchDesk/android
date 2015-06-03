package dk.shape.churchdesk;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.InjectView;
import dk.shape.churchdesk.view.EventDetailsView;
import dk.shape.churchdesk.view.NewEventView;
import dk.shape.churchdesk.viewmodel.EventDetailsViewModel;

/**
 * Created by Martin on 02/06/2015.
 */
public class EventDetailsActivity extends BaseLoggedInActivity {

    private MenuItem mMenuCreateEvent;


    @InjectView(R.id.content_view)
    protected EventDetailsView mContentView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_edit, menu);
        mMenuCreateEvent = menu.findItem(R.id.menu_event_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_edit:
                //TODO: Link to edit event
                Log.d("ERRORERROR", "onClickEditEvent");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    protected void onUserAvailable() {
        Bundle bundle = getIntent().getExtras();
        EventDetailsViewModel viewModel = new EventDetailsViewModel(_user, bundle.getInt("event"));
        viewModel.bind(mContentView);
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
}
