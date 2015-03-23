package dk.shape.churchdesk.network;

import com.squareup.okhttp.Request;

/**
 * Created by steffenkarlsson on 23/12/14.
 */
public abstract class PutRequest<T> extends AbstractBodyRequest<T> {

    protected PutRequest(String url) {
        super(url);
    }

    @Override
    protected Request finalizeRequest(Request.Builder builder) {
        builder.put(getData());
        return builder.build();
    }
}
