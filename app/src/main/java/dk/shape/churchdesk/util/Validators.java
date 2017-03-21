package dk.shape.churchdesk.util;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

/**
 * Created by steffenkarlsson on 26/01/15.
 */
public class Validators {

    public static boolean isValidEmail(EditText email) {
        String sEmail = email.getText().toString();
        return (!TextUtils.isEmpty(sEmail)
                && Patterns.EMAIL_ADDRESS.matcher(sEmail).matches());
    }

    public static boolean isValidPhone (EditText phone){
        String sPhone = phone.getText().toString();
        return (!TextUtils.isEmpty(sPhone) && Patterns.PHONE.matcher(sPhone).matches());
    }

    public static boolean isValidPassword(EditText password) {
        return password.getText().length() > 3;
    }

}
