package dk.shape.churchdesk.request;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Site;
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

    private static URLBuilder commentBuilder() {
        return authenticatedApiBuilder("comments");
    }

    private static URLBuilder pushNotificationBuilder() {
        return authenticatedApiBuilder("push-notifications");
    }

    private static URLBuilder eventsBuilder() {
        return authenticatedApiBuilder("events");
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

    public static String getRefreshTokenUrl(Context context, String refreshToken) {
        return oauthBuilder("token")
                .addParameter("refresh_token", refreshToken)
                .addParameter("client_id", context.getString(R.string.api_client_id))
                .addParameter("client_secret", context.getString(R.string.api_client_secret))
                .addParameter("grant_type", "refresh_token")
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

    public static String getMessageComments(int messageId, String site) {
        return messageBuilder().subdomain("/" + String.valueOf(messageId))
                .addParameter("site", site)
                .build();
    }

    public static String getUnreadMessageUrl() {
        return messageBuilder().subdomain("/unread").build();
    }

    public static String getDatabaseUrl() {
        return authenticatedApiBuilder("dictionaries").build();
    }

    public static String getCreateMessageUrl() {
        return messageBuilder().build();
    }

    public static String getCreateCommentUrl() {
        return commentBuilder().build();
    }

    public static String getEventsUrl(int year, int month) {
        return eventsBuilder()
                .subdomain(String.format("/%d/%d", year, month))
                .build();
    }

    public static String getHolydayUrl(int year) {
        return new URLBuilder()
                .subdomain(String.format("holydays/%d", year))
                .build();
    }

    public static String getTodayEventsUrl() {
        Calendar now = Calendar.getInstance();
        return eventsBuilder()
                .subdomain(String.format("/%d/%d", now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1))
                .build();
    }

    public static String getInvitesUrl() {
        return authenticatedApiBuilder("my-invites").build();
    }

    public static String getPushNotificationUrl() {
        return pushNotificationBuilder().subdomain("/settings").build();
    }

    public static String getMarkMessageAsReadUrl(int siteId, String site) {
        return messageBuilder()
                .subdomain(String.format("/%d/mark-as-read", siteId))
                .addParameter("site", site)
                .build();
    }
}