package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class AccessToken {

    @SerializedName("access_token")
    public String mAccessToken;

    @SerializedName("refresh_token")
    protected String mRefreshToken;

    @SerializedName("expires_in")
    protected int mExpiredIn;

    public String getRefreshToken() {
        return mRefreshToken;
    }
}
