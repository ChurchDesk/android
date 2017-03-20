package dk.shape.churchdesk.network;

import android.content.ContentValues;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.Toast;




import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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
   // private List<NameValuePair> mParameters = new ArrayList<>();
    //List<Pair<String, String>> mParameters = new ArrayList<>();
    ContentValues mParameters = new ContentValues();
    //List< String> mParameters = new ArrayList<>();
    //HashMap<String, String> mParameters = new HashMap<String, String>();

    public URLBuilder setDebug() {
        this.mDebug = true;
        return this;
    }

    public URLBuilder subdomain(String subdomain) {
        this.mSubdomain += subdomain;
        return this;
    }

    public URLBuilder addParameter(String key, String value) {
       //mParameters.add(new Pair<>(key, value));
        //mParameters.put(key, value);
        mParameters.put(key, value);
        return this;
    }

    public String build() {

        String whatIsGoing = "";
        String result = "";
        String finalResult = "";
        String encodedString = "";
/*
        whatIsGoing = mParameters.toString();
        finalResult = whatIsGoing.substring(1, whatIsGoing.length()-1);
        try {
             encodedString = URLEncoder.encode(finalResult, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } */

        try {
            encodedString = getFormData(mParameters);
           // finalResult = encodedString.substring(1, whatIsGoing.length()-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        result = (mDebug ? DEBUG_HOST_NAME : HOST_NAME) + mSubdomain
                 //   + (mParameters.size() > 0 ? "?" + URLEncoder.encode(getFormData(mParameters), "utf-8") : "");
                 //   + (mParameters.size() > 0 ? "?" + URLEncoder.encode(finalResult, "utf-8") : "");
                //  + (mParameters.size() > 0 ? "?" + whatIsGoing : "");
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