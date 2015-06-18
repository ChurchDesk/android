package dk.shape.churchdesk;

import android.widget.Toast;

import org.apache.http.HttpStatus;

import butterknife.InjectView;
import butterknife.OnClick;
import dk.shape.churchdesk.entity.AccessToken;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.LoginRequest;
import dk.shape.churchdesk.request.ResetPasswordRequest;
import dk.shape.churchdesk.request.URLUtils;
import dk.shape.churchdesk.util.AccountUtils;
import dk.shape.churchdesk.util.Validators;
import dk.shape.churchdesk.view.ForgotPasswordDialog;
import dk.shape.churchdesk.widget.CustomEditText;

/**
 * Created by steffenkarlsson on 20/03/15.
 */
public class StartActivity extends BaseActivity implements ForgotPasswordDialog.ForgotPasswordListener {

    private enum RequestType {
        LOGIN_REQUEST,
        RESET_PASSWORD
    }

    @InjectView(R.id.edit_email)
    protected CustomEditText mEmailField;

    @InjectView(R.id.edit_password)
    protected CustomEditText mPasswordField;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_start;
    }

    @Override
    protected int getTitleResource() {
        return -1;
    }

    @Override
    protected boolean showActionBar() {
        return false;
    }

    @OnClick(R.id.btn_login)
    void onLoginClicked() {
        if (Validators.isValidEmail(mEmailField)
                && Validators.isValidPassword(mPasswordField)) {
            new LoginRequest(this, mEmailField.getText().toString(),
                    mPasswordField.getText().toString())
                    .withContext(this)
                    .setOnRequestListener(listener)
                    .run(RequestType.LOGIN_REQUEST);
        } else {
            //TODO: Handle not valid email / password
        }
    }

    @OnClick(R.id.forgotPassword)
    protected void onForgotPasswordClicked() {
        ForgotPasswordDialog dialog = new ForgotPasswordDialog(this, this);
        dialog.show();
    }

    @Override
    public void onForgotPasswordClicked(String email) {
        new ResetPasswordRequest(email)
                .withContext(this)
                .setOnRequestListener(listener)
                .run(RequestType.RESET_PASSWORD);
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
            RequestType type = RequestHandler.<RequestType>getRequestIdentifierFromId(id);

            switch(type) {
                case RESET_PASSWORD:
                    Toast.makeText(StartActivity.this, R.string.forgot_password_request_error, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK && result.response != null) {
                switch (RequestHandler.<RequestType>getRequestIdentifierFromId(id)) {
                    case LOGIN_REQUEST:
                        AccessToken accessToken = (AccessToken) result.response;
                        URLUtils.setAccessToken(accessToken.mAccessToken);
                        AccountUtils.getInstance(StartActivity.this).saveToken(accessToken);
                        startActivity(getActivityIntent(StartActivity.this, MainActivity.class));
                        break;

                    case RESET_PASSWORD:
                        Toast.makeText(StartActivity.this, R.string.forgot_password_success, Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        }

        @Override
        public void onProcessing() {

        }
    };
}
