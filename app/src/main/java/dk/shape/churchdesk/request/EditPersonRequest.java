package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PutRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by chirag on 22/02/2017.
 */
public class EditPersonRequest extends PutRequest<Object> {

    CreatePersonRequest.PersonParameter mPersonObj;

    public EditPersonRequest(int personId, String site, CreatePersonRequest.PersonParameter parameter) {
        super(URLUtils.getEditEventUrl(personId, site));
        this.mPersonObj = parameter;
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        String data = parse(mPersonObj);
        return RequestBody.create(json, data);
    }

    @Override
    protected Object parseHttpResponseBody(String body) throws ParserException {
        return null;
    }

}
