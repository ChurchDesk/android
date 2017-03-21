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


import org.parceler.Parcels;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.HttpStatusCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.CreateEventRequest;
import dk.shape.churchdesk.request.EditEventRequest;
import dk.shape.churchdesk.view.DoubleBookingDialog;
import dk.shape.churchdesk.view.NewAbsenceView;
import dk.shape.churchdesk.viewmodel.NewAbsenceViewModel;

/**
 * Created by chirag on 23/02/16.
 */
public class NewAbsenceActivity extends BaseLoggedInActivity {

    private MenuItem mMenuCreateEvent;
    private MenuItem mMenuSaveEvent;
    private CreateEventRequest.EventParameter mEventParameter;

    public static String KEY_EVENT_EDIT = "KEY_EDIT_EVENT";
    Event _event;


    @InjectView(R.id.content_view)
    protected NewAbsenceView mContentView;

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
            if (mEventParameter.mUsers != null && mEventParameter.mUsers.size() > 0)
            {
                AlertDialog.Builder sendNotificationDialog = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                sendNotificationDialog.setTitle(R.string.sendNotificationsTitle);
                sendNotificationDialog.setMessage(R.string.sendNotificationsDialogMessage);
                sendNotificationDialog.setNegativeButton(R.string.menu_event_save_title,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mEventParameter.mSendNotifications = false;
                                saveEvent();
                            }
                        });
                sendNotificationDialog.setPositiveButton(R.string.save_and_send,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mEventParameter.mSendNotifications = true;
                                saveEvent();
                            }
                        });
                sendNotificationDialog.show();
            } else {
                mEventParameter.mSendNotifications = false;
                saveEvent();
            }
        }
        Log.d("ERRORERROR 1", "onClickAddEvent");
    }

    private void saveEvent(){
        new CreateEventRequest(mEventParameter)
                .withContext(this)
                .setOnRequestListener(listener)
                .run();
        setEnabled(mMenuCreateEvent, false);
        showProgressDialog(R.string.new_absence_create_progress, false);
    }

    private void editEvent() {
        if (mEventParameter != null) {
            new EditEventRequest(_event.getId(), _event.mSiteUrl, mEventParameter)
                    .withContext(this)
                    .setOnRequestListener(listener)
                    .run();
            setEnabled(mMenuSaveEvent, false);
            showProgressDialog(R.string.edit_absence_edit_progress, false);
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
        NewAbsenceViewModel viewModel = new NewAbsenceViewModel(_user, mSendOKListener);
        viewModel.bind(mContentView);
        if (_event != null) {
            viewModel.setDataToEdit(_event);
        }
    }

    private NewAbsenceViewModel.SendOkayListener mSendOKListener = new NewAbsenceViewModel.SendOkayListener() {
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

            if (errorCode == ErrorCode.BOOKING_CONFLICT && errorCode.sConflictHtml != null) {
                showDoublebookingDialog(errorCode.sConflictHtml);
            } else {
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), _event == null ? R.string.new_event_create_error : R.string.edit_event_edit_error, Toast.LENGTH_SHORT).show();
                setEnabled(mMenuCreateEvent, true);
            }
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatusCode.SC_OK
                    || result.statusCode == HttpStatusCode.SC_CREATED
                    || result.statusCode == HttpStatusCode.SC_NO_CONTENT) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NewAbsenceActivity.this);
                prefs.edit().putBoolean("newEvent", true).commit();
                prefs.edit().putBoolean("newCalendarEvent", true).commit();
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

        Site currentSite =  _user.getSiteByUrl(mEventParameter.mSite);
        boolean ifAllowedDoubleBook = currentSite.mPermissions.get("canDoubleBook");

        //if not allowed to make double booking then cannot make an absence
        if (ifAllowedDoubleBook == false)
        {
            dialog.hideAllowDoubleBookingButton();
            dialog.setTitleText("You are not allowed to make double bookings");
        }


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
                    saveEvent();
                } else {
                    editEvent();
                }
                dialog.dismiss();
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1s = 1000ms
                dismissProgressDialog();
                dialog.show();
            }
        }, 500);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_new_absence;
    }

    @Override
    protected int getTitleResource() {
        return _event == null ? R.string.new_absence_title : R.string.edit_absence_title;
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
