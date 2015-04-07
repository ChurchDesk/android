package dk.shape.churchdesk.fragment;

import android.support.v7.widget.SwitchCompat;
import android.view.View;

import org.apache.http.HttpStatus;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.PushNotification;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetPushNotificationSettingsRequest;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class SettingsFragment extends BaseFragment {

    private enum RequestTypes {
        GET_SETTINGS, SAVE_SETTINGS
    }

    @InjectView(R.id.notifications_events_created)
    protected SwitchCompat mEventsCreated;

    @InjectView(R.id.notifications_events_updates)
    protected SwitchCompat mEventsUpdates;

    @InjectView(R.id.notifications_events_cancels)
    protected SwitchCompat mEventsCancels;

    @InjectView(R.id.notifications_new_message)
    protected SwitchCompat mNewMessage;

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
        new GetPushNotificationSettingsRequest()
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.GET_SETTINGS);
    }

    @Override
    protected void onCreateView(View rootView) {
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    && result.response != null) {
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case GET_SETTINGS:
                        updateSwitches((PushNotification) result.response);
                        break;
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
        mEventsCreated.setChecked(pushNotification.isBookingCreated);
        mEventsUpdates.setChecked(pushNotification.isBookingUpdated);
        mEventsCancels.setChecked(pushNotification.isBookingCanceled);
        mNewMessage.setChecked(pushNotification.isMessage);
    }
}
