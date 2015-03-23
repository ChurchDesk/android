package dk.shape.churchdesk.request;

import android.content.Context;

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

    public static String getLoginUrl(Context context, String username, String password) {
        return oauthBuilder("token")
                .addParameter("client_id", context.getString(R.string.api_client_id))
                .addParameter("client_secret", context.getString(R.string.api_client_secret))
                .addParameter("grant_type", "password")
                .addParameter("username", username)
                .addParameter("password", password)
                .build();
    }

    public static String getUserUrl() {
        return authenticatedApiBuilder("users").build();
    }
}