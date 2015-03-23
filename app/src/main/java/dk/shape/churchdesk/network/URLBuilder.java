package dk.shape.churchdesk.network;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steffenkarlsson on 23/12/14.
 */
public class URLBuilder {

    private boolean mDebug = false;
    private final static String HOST_NAME = "http://api.churchdesk.com/";
    private final static String DEBUG_HOST_NAME = "http://private-anon-83c43a3ef-churchdeskapi.apiary-mock.com/";
    private String mSubdomain = "";
    private List<NameValuePair> mParameters = new ArrayList<>();

    public URLBuilder setDebug() {
        this.mDebug = true;
        return this;
    }

    public URLBuilder subdomain(String subdomain) {
        this.mSubdomain += subdomain;
        return this;
    }

    public URLBuilder addParameter(String key, String value) {
        mParameters.add(new BasicNameValuePair(key, value));
        return this;
    }

    public String build() {
        return (mDebug ? DEBUG_HOST_NAME : HOST_NAME) + mSubdomain
                + (mParameters.size() > 0 ? "?" + URLEncodedUtils.format(mParameters, "utf-8") : "");
    }
}