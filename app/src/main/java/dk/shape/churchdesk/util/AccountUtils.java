package dk.shape.churchdesk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 30/01/15.
 */
public class AccountUtils {

    private static final String KEY_EMAIL = "KEY_EMAIL";
    private static final String KEY_TOKEN = "KEY_TOKEN";

    private static AccountUtils ourInstance = null;
    private static SharedPreferences sharedPreferences;

    public static AccountUtils getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new AccountUtils(context);
        return ourInstance;
    }

    private AccountUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public void saveAccount(String email, String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public boolean containsAccount() {
        return sharedPreferences.contains(KEY_EMAIL) &&
                sharedPreferences.contains(KEY_TOKEN);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    public Pair<String, String> getAccount() {
        if (containsAccount()) {
            return new Pair<>(sharedPreferences.getString(KEY_EMAIL, ""),
                    sharedPreferences.getString(KEY_TOKEN, ""));
        }
        return null;
    }
}
