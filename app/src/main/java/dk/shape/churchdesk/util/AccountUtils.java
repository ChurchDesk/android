package dk.shape.churchdesk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.AccessToken;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 30/01/15.
 */
public class AccountUtils {

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

    public void saveToken(AccessToken token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, parse(token));
        editor.apply();
    }

    public boolean containsAccount() {
        return sharedPreferences.contains(KEY_TOKEN);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    public AccessToken getAccount() {
        try {
            if (containsAccount()) {
                String accessToken = sharedPreferences.getString(KEY_TOKEN, "");
                if (accessToken != null && !accessToken.isEmpty())
                    return parse(AccessToken.class, accessToken);
            }
        } catch (ParserException ignore) {
        }
        return null;
    }
}
