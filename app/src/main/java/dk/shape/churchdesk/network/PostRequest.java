package dk.shape.churchdesk.network;

import com.squareup.okhttp.Request;

/**
 * Created by steffenkarlsson on 23/12/14.
 */
public abstract class PostRequest<T> extends AbstractBodyRequest<T> {

    protected PostRequest(String url) {
        super(url);
    }

    @Override
    protected Request finalizeRequest(Request.Builder builder) {
        builder.post(getData());
        return builder.build();
    }
}
