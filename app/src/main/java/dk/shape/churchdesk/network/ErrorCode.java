package dk.shape.churchdesk.network;

import java.util.Map;

/**
 * Created by steffenkarlsson on 23/12/14.
 */
public enum ErrorCode {
    PARSER_FAIL, NO_BODY, NO_NETWORK, NETWORK_ERROR,
    UNAUTHORIZED, INVALID, READING_LESS_THAN_PREVIOUS,
    PARAM_MISSING, EXCEPTION;

    public Map<String, ?> ext;
}
