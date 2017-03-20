package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Map;

/**
 * Created by steffenkarlsson on 23/03/15.
 */

@Parcel
public class Site {


    //@SerializedName("userId")
    //public int mUserId;

    @SerializedName("name")
    public String mSiteName;

    @SerializedName("attendenceEnabled")
    public boolean mAttendenceEnabled;

    @SerializedName("id")
    public String mSiteUrl;

    //probably needs to be deleted since not used(?) anywhere
    @SerializedName("organizationId")
    public Integer iOrganizationId;

    @SerializedName("permissions")
    public Map<String, Boolean> mPermissions;

    public String getId() {
        return mSiteUrl;
    }

    public boolean equals(String id) {
        return id.equals(this.mSiteUrl);
    }

    public Map<String, Boolean> getmPermissions() {
        return mPermissions;
    }




}
