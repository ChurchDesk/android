package dk.shape.churchdesk;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;

import org.parceler.Parcels;

import dk.shape.churchdesk.entity.User;
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
            // TODO: Set credentials some where
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

        Pair<String, String> emailToken = AccountUtils.getInstance(this).getAccount();
        if (emailToken == null) {
            goToLoginScreen();
            return;
        }

        if (setLoadUser()) {
            // TODO: Set credentials some where
            // TODO: GET User Request
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
}
