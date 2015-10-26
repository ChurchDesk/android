package dk.shape.churchdesk;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import dk.shape.churchdesk.entity.AccessToken;
import dk.shape.churchdesk.entity.PushNotification;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetPushNotificationSettingsRequest;
import dk.shape.churchdesk.request.GetUserRequest;
import dk.shape.churchdesk.request.RefreshTokenRequest;
import dk.shape.churchdesk.request.URLUtils;
import dk.shape.churchdesk.util.AccountUtils;
import dk.shape.churchdesk.util.DatabaseUtils;

/**
 * Created by steffenkarlsson on 20/03/15.
 */
public abstract class BaseLoggedInActivity extends BaseActivity {

    private enum RequestTypes {
        USER,
        REFRESH,
        GET_SETTINGS
    }

    public static final String KEY_USER = "KEY_USER";

    private ProgressDialog mProgressDialog;
    protected User _user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_USER))
                _user = Parcels.unwrap(extras.getParcelable(KEY_USER));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (_user != null)
            outState.putParcelable(KEY_USER, Parcels.wrap(_user));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(KEY_USER)) {
            _user = Parcels.unwrap(savedInstanceState.getParcelable(KEY_USER));
            if (_user != null){
                URLUtils.setAccessToken(_user.mAccessToken.mAccessToken);
            }
            else
                URLUtils.setAccessToken(AccountUtils.getInstance(this).getAccount().mAccessToken);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        prepareForLoading();
        verifyLoggedIn();
    }

    private void verifyLoggedIn() {
        if (_user != null) {
            onUserAvailable();
            return;
        }

        AccessToken accessToken = AccountUtils.getInstance(this).getAccount();
        if (accessToken == null) {
            goToLoginScreen();
            return;
        }

        onLoggedIn(accessToken);
    }

    private void onLoggedIn(AccessToken accessToken) {
        URLUtils.setAccessToken(accessToken.mAccessToken);
        DatabaseUtils.getInstance().init(this);

        if (setLoadUser()) {
            if (mProgressDialog == null || mProgressDialog.isShowing())
                showProgressDialog(R.string.loading, false);

            new GetUserRequest()
                    .withContext(this)
                    .setOnRequestListener(listener)
                    .runAsync(RequestTypes.USER);

        } else {
            onUserAvailable();
        }

    }

    protected void refreshAccessToken(String refreshToken) {
        showProgressDialog(R.string.loading, false);
        new RefreshTokenRequest(this, refreshToken)
                .withContext(this)
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.REFRESH);
    }

    protected void showProgressDialog(String message, boolean cancelable) {
        dismissProgressDialog();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(message);

        mProgressDialog.show();
    }

    protected void showProgressDialog(@StringRes int messageResId, boolean cancelable) {
        showProgressDialog(getString(messageResId), cancelable);
    }


    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            mProgressDialog = null;
        }
    }

    protected void onUserAvailable() {
        new GetPushNotificationSettingsRequest()
                .withContext(this)
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.GET_SETTINGS);
    }

    protected void prepareForLoading() {
        return;
    }

    protected boolean setLoadUser() {
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
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case USER:
                        _user = (User) result.response;
                        _user.mAccessToken = AccountUtils.getInstance(
                                BaseLoggedInActivity.this).getAccount();
                        dismissProgressDialog();
                        URLUtils.setUserId(_user.mUserId);
                        URLUtils.setOrganizationId(_user.mSites.get(0).mSiteUrl);
                        onUserAvailable();
                        return;
                    case REFRESH:
                        AccessToken token = (AccessToken) result.response;
                        AccountUtils.getInstance(BaseLoggedInActivity.this).saveToken(token);
                        onLoggedIn(token);
                        return;
                    case GET_SETTINGS:
                        _user.setNotifications((PushNotification) result.response);
                        break;
                }
            }
        }

        @Override
        public void onProcessing() {

        }
    };
}
