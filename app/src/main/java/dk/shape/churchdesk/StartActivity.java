package dk.shape.churchdesk;

import butterknife.InjectView;
import butterknife.OnClick;
import dk.shape.churchdesk.util.Validators;
import dk.shape.churchdesk.widget.CustomEditText;

/**
 * Created by steffenkarlsson on 20/03/15.
 */
public class StartActivity extends BaseActivity {

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
            
        } else {
            //TODO: Handle not valid email / password
        }
    }
}
