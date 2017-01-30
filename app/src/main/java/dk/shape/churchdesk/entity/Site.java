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

    @SerializedName("organizationId")
    public Integer iOrganizationId;

    @SerializedName("permissions")
    public Map<String, Boolean> mPermissions;

    public boolean equals(String id) {
        return id.equals(this.mSiteUrl);
    }

    //return all permisions in a Map
    public Map<String, Boolean> getAllPermissions()
    {
        return mPermissions;
    }



}
