package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Map;

import java.util.List;

/**
 * Created by steffenkarlsson on 23/03/15.
 */

@Parcel
public class Site {

    @SerializedName("userId")
    public int mUserId;

    @SerializedName("siteName")
    public String mSiteName;

    @SerializedName("attendenceEnabled")
    public boolean mAttendenceEnabled;

    @SerializedName("site")
    public String mSiteUrl;

    @SerializedName("mail")
    public String mEmail;

    @SerializedName("groups")
    public List<String> mGroupIds;

    @SerializedName("permissions")
    public Map<String, Boolean> mPermissions;

    public boolean equals(String id) {
        return id.equals(this.mSiteUrl);
    }
}
