package dk.shape.churchdesk.network;

/**
 * Created by steffenkarlsson on 23/12/14.
 */
public enum ErrorCode {
    PARSER_FAIL, NO_NETWORK, NETWORK_ERROR, BOOKING_CONFLICT,
    INVALID_GRANT, NOT_ACCEPTABLE, PAYMENT_REQUIRED;

    public String dec;
    public String sConflictHtml;
}
