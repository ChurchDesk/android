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

    @SerializedName("accessToken")
    public String mAccessToken;

}
