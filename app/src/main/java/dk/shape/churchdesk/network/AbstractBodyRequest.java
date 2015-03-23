package dk.shape.churchdesk.network;

import android.support.annotation.NonNull;
import com.squareup.okhttp.RequestBody;

/**
 * Created by steffenkarlsson on 22/12/14.
 */
public abstract class AbstractBodyRequest<T> extends BaseRequest<T> {

    private boolean receiveData = false;

    protected AbstractBodyRequest(String url) {
        super(url);
    }

    @NonNull
    protected abstract RequestBody getData();

    public BaseRequest shouldReturnData() {
        this.receiveData = true;
        return this;
    }

    @Override
    public Result<T> handleResponse(int statusCode, String body) throws ParserException {
        if (receiveData) {
            T result = parseHttpResponseBody(body);
            if (result == null)
                throw new ParserException();
            else
                return new Result<>(statusCode, result);
        } else {
            return new Result<>(statusCode, null);
        }
    }
}
