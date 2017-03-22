package dk.shape.churchdesk.network;



import android.content.ContentValues;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by steffenkarlsson on 23/12/14.
 */
public class URLBuilder {

    private boolean mDebug = false;
    //private final static String HOST_NAME = "http://192.168.1.2:3000/";
    private final static String HOST_NAME = "https://api2.churchdesk.com/";
    private final static String DEBUG_HOST_NAME = "http://f4b2527d.ngrok.io/";
    private String mSubdomain = "";
    ContentValues mParameters = new ContentValues();


    public URLBuilder setDebug() {
        this.mDebug = true;
        return this;
    }

    public URLBuilder subdomain(String subdomain) {
        this.mSubdomain += subdomain;
        return this;
    }

    public URLBuilder addParameter(String key, String value) {
        mParameters.put(key, value);
        return this;
    }

    public String build() {

        String result = "";
        String encodedString = "";

        try {
            encodedString = getFormData(mParameters);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        result = (mDebug ? DEBUG_HOST_NAME : HOST_NAME) + mSubdomain
                + (mParameters.size() > 0 ? "?" +encodedString : "");


        return  result;

    }

    public String getFormData(ContentValues contentValues) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : contentValues.valueSet()) {
            if (first)
                first = false;
            else
                sb.append("&");

            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }
        return sb.toString();
    }
}