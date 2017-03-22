package dk.shape.churchdesk.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;



import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import dk.shape.churchdesk.MainActivity;
import dk.shape.churchdesk.NewMessageActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.StartActivity;
import dk.shape.churchdesk.entity.PushNotification;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.HttpStatusCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.CreateMessageRequest;
import dk.shape.churchdesk.request.DeletePushNotificationTokenRequest;
import dk.shape.churchdesk.request.GetPushNotificationSettingsRequest;
import dk.shape.churchdesk.request.SavePushNotificationSettingsRequest;
import dk.shape.churchdesk.util.AccountUtils;

import io.intercom.android.sdk.Intercom;
/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class SettingsFragment extends BaseFragment {

    private enum RequestTypes {
        SAVE_SETTINGS
    }

    @InjectView(R.id.notifications_events_created)
    protected SwitchCompat mEventsCreated;

    @InjectView(R.id.notifications_events_updates)
    protected SwitchCompat mEventsUpdates;

    @InjectView(R.id.notifications_events_cancels)
    protected SwitchCompat mEventsCancels;

    @InjectView(R.id.notifications_new_message)
    protected SwitchCompat mNewMessage;

    private PushNotification mPushNotification;
    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    protected int getTitleResource() {
        return R.string.menu_settings;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void onUserAvailable() {
        updateSwitches(mPushNotification = _user.getNotifications());
    }

    @Override
    protected void onCreateView(View rootView) {
        setHasOptionsMenu(true);
        mEventsCreated.setOnCheckedChangeListener(changeListener);
        mEventsUpdates.setOnCheckedChangeListener(changeListener);
        mEventsCancels.setOnCheckedChangeListener(changeListener);
        mNewMessage.setOnCheckedChangeListener(changeListener);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_logout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle(R.string.logout_sure)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AccountUtils.getInstance(getActivity()).clear();
                                Intercom.client().reset();
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                String deviceToken = prefs.getString("deviceToken", "");
                                new DeletePushNotificationTokenRequest(deviceToken)
                                        .withContext(getActivity())
                                        .setOnRequestListener(listener)
                                        .runAsync();
                                showActivity(StartActivity.class, false, null);
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleTask() {
        if (mTimer != null && mTimerTask != null) {
            mTimer.cancel();
            mTimerTask.cancel();
        }
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                new SavePushNotificationSettingsRequest(mPushNotification)
                        .withContext(getActivity())
                        .setOnRequestListener(listener)
                        .runAsync(RequestTypes.SAVE_SETTINGS);
            }
        };
        mTimer.schedule(mTimerTask, 750);
    }

    private CompoundButton.OnCheckedChangeListener changeListener
            = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.notifications_events_created:
                    mPushNotification.isBookingCreated.put("push", isChecked);
                    break;
                case R.id.notifications_events_updates:
                    mPushNotification.isBookingUpdated.put("push", isChecked);
                    break;
                case R.id.notifications_events_cancels:
                    mPushNotification.isBookingCanceled.put("push", isChecked);
                    break;
                case R.id.notifications_new_message:
                    mPushNotification.isMessage.put("push", isChecked);
                    break;
            }
            handleTask();
        }
    };

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
            if (errorCode == ErrorCode.INVALID_GRANT){
                AccountUtils.getInstance(getActivity()).clear();
                Intercom.client().reset();
                showActivity(StartActivity.class, false, null);
                getActivity().finish();
            }
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatusCode.SC_OK
                    && result.response != null) {
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case SAVE_SETTINGS:
                        break;
                }
            }
        }

        @Override
        public void onProcessing() {
        }
    };

    private void updateSwitches(PushNotification pushNotification) {
        mEventsCreated.setChecked(pushNotification.isBookingCreated.get("push"));
        mEventsUpdates.setChecked(pushNotification.isBookingUpdated.get("push"));
        mEventsCancels.setChecked(pushNotification.isBookingCanceled.get("push"));
        mNewMessage.setChecked(pushNotification.isMessage.get("push"));
    }
}
