package dk.shape.churchdesk.request;

import dk.shape.churchdesk.entity.PushNotification;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
public class GetPushNotificationSettingsRequest extends GetRequest<PushNotification> {

    public GetPushNotificationSettingsRequest() {
        super(URLUtils.getPushNotificationUrl());
    }

    @Override
    protected PushNotification parseHttpResponseBody(String body) throws ParserException {
        return parse(PushNotification.class, body);
    }
}
