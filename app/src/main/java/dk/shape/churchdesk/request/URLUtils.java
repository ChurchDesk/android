package dk.shape.churchdesk.request;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.network.URLBuilder;

/**
 * Created by steffenkarlsson on 22/12/14.
 */
public class URLUtils {

    private static String token = "";

    public static void setAccessToken(String token) {
        URLUtils.token = token;
    }

    private static URLBuilder apiBuilder(String target) {
        return new URLBuilder().subdomain("api/v1/" + target);
    }

    private static URLBuilder oauthBuilder(String target) {
        return new URLBuilder().subdomain("oauth/v2/" + target);
    }

    private static URLBuilder authenticatedApiBuilder(String target) {
        return apiBuilder(target).addParameter("access_token", token);
    }

    private static URLBuilder messageBuilder() {
        return authenticatedApiBuilder("messages");
    }

    public static String getLoginUrl(Context context, String username, String password) {
        return oauthBuilder("token")
                .addParameter("client_id", context.getString(R.string.api_client_id))
                .addParameter("client_secret", context.getString(R.string.api_client_secret))
                .addParameter("grant_type", "password")
                .addParameter("username", username)
                .addParameter("password", password)
                .build();
    }

    public static String getCurrentUserUrl() {
        return authenticatedApiBuilder("users").build();
    }

    private static SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
    public static String getMessages(Date startDate) {
        return messageBuilder()
                .addParameter("limit", String.valueOf(50))
                .addParameter("start_date", formatter.format(startDate))
                .build();
    }

    public static String getDatabaseUrl() {
        return authenticatedApiBuilder("dictionaries").build();
    }
}