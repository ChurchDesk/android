package dk.shape.churchdesk.request;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

import dk.shape.churchdesk.entity.AccessToken;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class LoginRequest extends PostRequest<Object> {

    private LoginParameter mLoginObj;

    public LoginRequest(Context context, String username, String password) {
        super(URLUtils.getLoginUrl(context, username, password));
        this.mLoginObj = new LoginParameter(username, password);
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        String data = parse(mLoginObj);
        return RequestBody.create(json, data);
    }

    @Override
    protected AccessToken parseHttpResponseBody(String body) throws ParserException {
        AccessToken token = parse(AccessToken.class, body);
        return token;
    }

    public static class LoginParameter {

        public LoginParameter(String username, String password) {
            this.mUsername = username;
            this.mPassword = password;
            this.sApp = "mobile";
        }

        @SerializedName("username")
        public String mUsername;

        @SerializedName("password")
        public String mPassword;

        @SerializedName("app")
        public String sApp;

    }
}
