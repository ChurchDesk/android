package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by steffenkarlsson on 23/03/15.
 */

@Parcel
public class AccessToken {

    @SerializedName("access_token")
    public String mAccessToken;

    @SerializedName("refresh_token")
    protected String mRefreshToken;

    @SerializedName("expires_in")
    protected int mExpiredIn;

    protected Calendar mExpiresAt;

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void calculateExpirationDate() {
        mExpiresAt = Calendar.getInstance();
        mExpiresAt.add(Calendar.SECOND, mExpiredIn - (int) TimeUnit.MINUTES.toSeconds(5));
    }

    public boolean isExpired() {
        return mExpiresAt.before(Calendar.getInstance());
    }
}
