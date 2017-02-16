package dk.shape.churchdesk.request;

import android.content.Context;
import android.text.LoginFilter;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.network.URLBuilder;
import hirondelle.date4j.DateTime;


/**
 * Created by steffenkarlsson on 22/12/14.
 */
public class URLUtils {

    private static String token = "";

    public static void setAccessToken(String token) {
        URLUtils.token = token;
    }

    private static String userId = "";

    public static void setUserId(String userId) {
        URLUtils.userId = userId;
    }

    private static String organizationId = "";

    public static void setOrganizationId(String organizationId) {
        URLUtils.organizationId = organizationId;
    }

    private static URLBuilder apiBuilder(String target) {
        return new URLBuilder().subdomain("" + target);
    }

    private static URLBuilder oauthBuilder(String target) {
        return new URLBuilder().subdomain("" + target);
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
        return authenticatedApiBuilder("");
    }

    private static URLBuilder eventsBuilder() {
        return authenticatedApiBuilder("calendar");
    }

    private static URLBuilder peopleBuilder() {
        return authenticatedApiBuilder("people/people");
    }

    private static URLBuilder segmentsBuilder() {
        return authenticatedApiBuilder("people/segments");
    }

    public static String getLoginUrl(Context context, String username, String password) {
        return oauthBuilder("login")
                .build();
    }

    public static String getTokenUrl(Context context) {
        return oauthBuilder("token")
                .addParameter("client_id", context.getString(R.string.api_client_id))
                .addParameter("client_secret", context.getString(R.string.api_client_secret))
                .addParameter("grant_type", "client_credentials")
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
        return authenticatedApiBuilder("users/me").build();
    }

    private static SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public static String getMessages(Date startDate) {
        return messageBuilder()
                .addParameter("limit", String.valueOf(50))
                .addParameter("limitDate", formatter.format(startDate))
                .build();
    }

    public static String getMessagesSearchUrl(Date startDate, String query) {
        return messageBuilder()
                .addParameter("limitDate", formatter.format(startDate))
                .addParameter("limit", String.valueOf(50))
                .addParameter("search", query)
                .build();
    }

    public static String getMessageComments(int messageId, String site) {
        return messageBuilder().subdomain("/" + String.valueOf(messageId))
                .addParameter("site", site)
                .build();
    }

    public static String getUnreadMessageUrl() {
        return messageBuilder().addParameter("onlyUnread", "1").build();
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

    public static String getCreateMessageCommentUrl(int messageId) {
        return messageBuilder().subdomain("/" + Integer.toString(messageId)).subdomain("/comments").build();
    }

    public static String getEventsUrl(int year, int month) {

        month = month == 0 ? 1 : month;

        String start = String.format("%d-%d-01", year, month);
        DateTime dateTime = new DateTime(year, month, 14, 12, 0, 0, 000);
        // Get the number of days in that month
        int daysInMonth = dateTime.getEndOfMonth().getNumDaysInMonth();
        String end = String.format("%d-%d-%d", year, month, daysInMonth);
        return getEventsRange(start, end);
    }

    public static String getPeopleUrl(String organizationId, List <String> segmentIds) {
        return peopleBuilder()
                .addParameter("organizationId", organizationId)
                .build();
    }

    public static String getSegmentsUrl(String organizationId) {
        return segmentsBuilder()
                .addParameter("organizationId", organizationId)
                .build();
    }

    // Return the url for the range.
    public static String getEventsRange(String startDate, String endDate) {
        return eventsBuilder()
                .addParameter("start", startDate)
                .addParameter("end", endDate)
                .addParameter("showBusyEvents", "false")
                .build();
    }

    public static String getHolydayUrl(int year, String language) {
        return authenticatedApiBuilder(String.format("calendar/holydays/%s/%d", language, year)).build();
    }

    public static String getTodayEventsUrl() {
        // Get only today's events.
        Calendar start = Calendar.getInstance();
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DATE, 1);
        // Date formatter.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Ask only for single day events.
        return  getEventsRange(dateFormat.format(start.getTime()), dateFormat.format(end.getTime()));
    }

    public static String getInvitesUrl() {
        return authenticatedApiBuilder("calendar/invitations").build();
    }

    public static String getPushNotificationUrl() {
        String pushNotificationUrl = "users/" +  URLUtils.userId;
        return pushNotificationBuilder().subdomain(pushNotificationUrl).addParameter("organizationId", URLUtils.organizationId).build();
    }

    public static String getSendPushNotificationTokenUrl(String token, String devType) {
        return pushNotificationBuilder()
                .subdomain(String.format("devices/%s/android/%s", token, devType))
                .build();
    }

    public static String getDeletePushNotificationTokenUrl(String token) {
        return pushNotificationBuilder()
                .subdomain(String.format("devices/%s", token))
                .build();
    }

    public static String getMarkMessageAsReadUrl(int siteId, String site) {
        return messageBuilder()
                .subdomain(String.format("/%d/mark-as-read", siteId))
                .addParameter("site", site)
                .build();
    }

    public static String getCreateEventUrl(String site){
        return eventsBuilder()
                .addParameter("organizationId", site).
                build();
    }

    public static String getSingleEvent(int eventId, String site){
        return eventsBuilder()
                .subdomain(String.format("/%d", eventId))
                .addParameter("organizationId", site)
                .build();
    }

    public static String getCreateResponseUrl(int eventId, String response, String site){
        return eventsBuilder()
                .subdomain(String.format("/invitations/%d/attending/%s", eventId, response))
                .addParameter("organizationId", site)
                .build();
    }

    public static String getEditEventUrl(int eventId, String site){
        return eventsBuilder()
                .subdomain(String.format("/%d", eventId))
                .addParameter("organizationId", site)
                .build();
    }

    public static String getUpdateCommentUrl(int commentId, String site){
        return messageBuilder()
                .subdomain("/comments")
                .subdomain(String.format("/%d", commentId))
                .addParameter("organizationId", site)
                .build();
    }


    public static String getDeleteCommentUrl(String site, int commentId){
        return messageBuilder()
                .subdomain("/comments")
                .subdomain(String.format("/%d", commentId))
                .addParameter("organizationId", site)
                .build();
    }

    public static String getResetPasswordUrl() {
        return oauthBuilder("login")
                .subdomain("/forgot")
                .build();
    }
}