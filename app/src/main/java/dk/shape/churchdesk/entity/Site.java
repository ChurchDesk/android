package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Map;

/**
 * Created by steffenkarlsson on 23/03/15.
 */

@Parcel
public class Site {

    @SerializedName("userId")
    public String mUserId;

    @SerializedName("siteName")
    public String mSiteName;

    @SerializedName("attendenceEnabled")
    public boolean mAttendenceEnabled;

    @SerializedName("site")
    public String mSiteUrl;

    @SerializedName("permissions")
    public Map<String, Boolean> mPermissions;
}
