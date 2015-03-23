package dk.shape.churchdesk.network;

import java.io.IOException;

/**
 * Created by steffenkarlsson on 22/12/14.
 */
public class ParserException extends IOException {

    public ParserException(String detailMessage) {
        super(detailMessage);
    }

    public ParserException() {
    }
}