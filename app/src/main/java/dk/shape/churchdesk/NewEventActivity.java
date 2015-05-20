package dk.shape.churchdesk;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.InjectView;
import dk.shape.churchdesk.view.NewEventView;
import dk.shape.churchdesk.viewmodel.NewEventViewModel;

/**
 * Created by Martin on 20/05/2015.
 */
public class NewEventActivity extends BaseLoggedInActivity{

    private MenuItem mMenuCreateEvent;


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
                Log.d("ERRORERROR", "onClickAddEvent");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onUserAvailable() {
        NewEventViewModel viewModel = new NewEventViewModel();
        viewModel.bind(mContentView);
    }

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
