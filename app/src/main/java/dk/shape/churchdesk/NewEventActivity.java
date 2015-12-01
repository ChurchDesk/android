package dk.shape.churchdesk;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.*;
import dk.shape.churchdesk.fragment.DashboardFragment;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.CreateEventRequest;
import dk.shape.churchdesk.request.EditEventRequest;
import dk.shape.churchdesk.view.DoubleBookingDialog;
import dk.shape.churchdesk.view.NewEventView;
import dk.shape.churchdesk.viewmodel.NewEventViewModel;

/**
 * Created by Martin on 20/05/2015.
 */
public class NewEventActivity extends BaseLoggedInActivity {

    private MenuItem mMenuCreateEvent;
    private MenuItem mMenuSaveEvent;
    private CreateEventRequest.EventParameter mEventParameter;

    public static String KEY_EVENT_EDIT = "KEY_EDIT_EVENT";
    Event _event;


    @InjectView(R.id.content_view)
    protected NewEventView mContentView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_add, menu);
        mMenuCreateEvent = menu.findItem(R.id.menu_event_add);
        mMenuSaveEvent = menu.findItem(R.id.menu_event_save);
        setEnabled(mMenuCreateEvent, false);
        if (_event != null) {
            mMenuCreateEvent.setVisible(false);
            mMenuSaveEvent.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_add:
                createNewEvent();
                return true;
            case R.id.menu_event_save:
                editEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewEvent() {
        if (mEventParameter != null) {
            new CreateEventRequest(mEventParameter)
                    .withContext(this)
                    .setOnRequestListener(listener)
                    .run();
            setEnabled(mMenuCreateEvent, false);
            showProgressDialog(R.string.new_event_create_progress, false);
        }
        Log.d("ERRORERROR 1", "onClickAddEvent");
    }

    private void editEvent() {
        if (mEventParameter != null) {
            new EditEventRequest(_event.getId(), _event.mSiteUrl, mEventParameter)
                    .withContext(this)
                    .setOnRequestListener(listener)
                    .run();
            setEnabled(mMenuSaveEvent, false);
            showProgressDialog(R.string.edit_event_edit_progress, false);
            // We need to refresh the calendar.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_EVENT_EDIT)) {
                _event = Parcels.unwrap(extras.getParcelable(KEY_EVENT_EDIT));
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();
        NewEventViewModel viewModel = new NewEventViewModel(_user, mSendOKListener);
        viewModel.bind(mContentView);
        if (_event != null) {
            viewModel.setDataToEdit(_event);
        }
    }

    private NewEventViewModel.SendOkayListener mSendOKListener = new NewEventViewModel.SendOkayListener() {
        @Override
        public void okay(boolean isOkay, CreateEventRequest.EventParameter parameter) {
            setEnabled(mMenuCreateEvent, isOkay);
            setEnabled(mMenuSaveEvent, isOkay);
            if (isOkay) {
                mEventParameter = parameter;
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
            if (errorCode == ErrorCode.BOOKING_CONFLICT && errorCode.sConflictHtml != null) {
                showDoublebookingDialog(errorCode.sConflictHtml);
            } else {
                Toast.makeText(getApplicationContext(), _event == null ? R.string.new_event_create_error : R.string.edit_event_edit_error, Toast.LENGTH_SHORT).show();
                setEnabled(mMenuCreateEvent, true);
            }
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    || result.statusCode == HttpStatus.SC_CREATED
                    || result.statusCode == HttpStatus.SC_NO_CONTENT) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NewEventActivity.this);
                prefs.edit().putBoolean("newEvent", true).commit();
                finish();
            }
            dismissProgressDialog();
        }

        @Override
        public void onProcessing() {
        }
    };

    private void showDoublebookingDialog(String des) {
        final DoubleBookingDialog dialog = new DoubleBookingDialog(this, des, R.string.edit_event_dialog_double_booking);
        dialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnAllowClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEventParameter.isAllowDoubleBooking = true;
                if (_event == null) {
                    createNewEvent();
                } else {
                    editEvent();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_new_event;
    }

    @Override
    protected int getTitleResource() {
        return _event == null ? R.string.new_event_title : R.string.edit_event_title;
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
