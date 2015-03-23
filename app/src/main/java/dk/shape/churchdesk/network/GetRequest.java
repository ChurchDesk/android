package dk.shape.churchdesk.network;

import com.squareup.okhttp.Request;

/**
 * Created by steffenkarlsson on 23/12/14.
 */
public abstract class GetRequest<T> extends AbstractNoBodyRequest<T> {

    protected GetRequest(String url) {
        super(url);
    }

    @Override
    protected Request finalizeRequest(Request.Builder builder) {
        builder = builder.get();
        return builder.build();
    }
}
