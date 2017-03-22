package dk.shape.churchdesk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;



import butterknife.InjectView;
import butterknife.OnClick;
import dk.shape.churchdesk.entity.AccessToken;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.HttpStatusCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetTokenRequest;
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
        RESET_PASSWORD,
        GET_TOKEN
    }

    @InjectView(R.id.edit_email)
    protected CustomEditText mEmailField;

    @InjectView(R.id.edit_password)
    protected CustomEditText mPasswordField;

    private ProgressDialog _progress;

    private String mEmailToBeReset;

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
        if(!Validators.isValidEmail(mEmailField)) {
            mEmailField.setError(getString(R.string.login_email_validation_error));
        }

        if(!Validators.isValidPassword(mPasswordField)) {
            mPasswordField.setError(getString(R.string.login_password_validation_error));
        }

        if (Validators.isValidEmail(mEmailField) && Validators.isValidPassword(mPasswordField)) {
            _progress = ProgressDialog.show(this, getString(R.string.login_progress_title), getString(R.string.login_progress_message), true, false);

            new LoginRequest(this, mEmailField.getText().toString(),
                    mPasswordField.getText().toString()).shouldReturnData()
                    .withContext(this)
                    .setOnRequestListener(listener)
                    .run(RequestType.LOGIN_REQUEST);
        }
    }

    @OnClick(R.id.forgotPassword)
    protected void onForgotPasswordClicked() {
        ForgotPasswordDialog dialog = new ForgotPasswordDialog(this, this);
        dialog.show();
    }

    private void dismissProgress() {
        if(_progress != null) {
            _progress.dismiss();
            _progress = null;
        }
    }

    @Override
    public void onForgotPasswordClicked(String email) {
        mEmailToBeReset = email;
        new ResetPasswordRequest(mEmailToBeReset)
                .shouldReturnData()
                .withContext(StartActivity.this)
                .setOnRequestListener(listener)
                .run(RequestType.RESET_PASSWORD);
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
            dismissProgress();

            if (errorCode == ErrorCode.BLOCKED_USER) {
                new AlertDialog.Builder(StartActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle(R.string.user_blocked_login_title)
                        .setMessage(R.string.user_blocked_login_text)
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            if (errorCode == ErrorCode.NOT_ACCEPTABLE || errorCode == ErrorCode.PAYMENT_REQUIRED ){
                new AlertDialog.Builder(StartActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle(R.string.payment_required)
                        .setMessage(R.string.payment_description)
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else if (errorCode == ErrorCode.INVALID_GRANT){
                Toast.makeText(StartActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }

            switch(RequestHandler.<RequestType>getRequestIdentifierFromId(id)) {
                case RESET_PASSWORD:
                case GET_TOKEN:
                    new AlertDialog.Builder(StartActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle(R.string.forgot_password_request_error_header)
                            .setMessage(R.string.forgot_password_request_error_text)
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    break;
            }
        }

        @Override
        public void onSuccess(int id, Result result) {
            dismissProgress();
            Log.d("on success", RequestHandler.<RequestType>getRequestIdentifierFromId(id).toString());
            Log.d("response received", result.response.toString() + result.statusCode);
            if (result.statusCode == HttpStatusCode.SC_OK && result.response != null) {
                switch (RequestHandler.<RequestType>getRequestIdentifierFromId(id)) {
                    case LOGIN_REQUEST: {
                        Log.d("on success", result.response.toString());
                        AccessToken accessToken = (AccessToken) result.response;
                        URLUtils.setAccessToken(accessToken.mAccessToken);

                        Log.d("token", accessToken.mAccessToken);
                        AccountUtils.getInstance(StartActivity.this).saveToken(accessToken);
                        startActivity(getActivityIntent(StartActivity.this, MainActivity.class));
                        break;
                    } case GET_TOKEN: {
                        AccessToken accessToken = (AccessToken) result.response;
                        URLUtils.setAccessToken(accessToken.mAccessToken);

                        new ResetPasswordRequest(mEmailToBeReset)
                                .shouldReturnData()
                                .withContext(StartActivity.this)
                                .setOnRequestListener(listener)
                                .run(RequestType.RESET_PASSWORD);
                        break;
                    } case RESET_PASSWORD:
                        new AlertDialog.Builder(StartActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                .setTitle(R.string.forgot_password_success_header)
                                .setMessage(getString(R.string.forgot_password_success_text, mEmailToBeReset))
                                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                        break;
                }
            }
        }

        @Override
        public void onProcessing() {

        }
    };
}
