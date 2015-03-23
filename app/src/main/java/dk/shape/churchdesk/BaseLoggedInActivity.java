package dk.shape.churchdesk;

import android.os.Bundle;
import android.support.annotation.NonNull;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import dk.shape.churchdesk.entity.AccessToken;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetUserRequest;
import dk.shape.churchdesk.request.URLUtils;
import dk.shape.churchdesk.util.AccountUtils;

/**
 * Created by steffenkarlsson on 20/03/15.
 */
public abstract class BaseLoggedInActivity extends BaseActivity {

    protected final String KEY_USER = "KEY_USER";

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
            if (_user != null)
                URLUtils.setAccessToken(_user.mAccessToken.mAccessToken);
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
        }

        AccessToken accessToken = AccountUtils.getInstance(this).getAccount();
        if (accessToken == null) {
            goToLoginScreen();
            return;
        }

        if (setLoadUser()) {
            URLUtils.setAccessToken(accessToken.mAccessToken);
            new GetUserRequest()
                    .withContext(this)
                    .setOnRequestListener(listener)
                    .runAsync();
        } else {
            onUserAvailable();
        }
    }

    protected abstract void onUserAvailable();

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
                _user = (User) result.response;
                _user.mAccessToken = AccountUtils.getInstance(
                        BaseLoggedInActivity.this).getAccount();
                onUserAvailable();
                return;
            }
        }

        @Override
        public void onProcessing() {

        }
    };
}
