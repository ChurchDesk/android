package dk.shape.churchdesk.entity.resources;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class OtherUser {

    @SerializedName("id")
    protected String id;

    @SerializedName("name")
    public String mName;

    @SerializedName("status")
    protected int mStatus;

    @SerializedName("picture")
    public String mPictureUrl;

    @SerializedName("roles")
    public List<Integer> mRoles;

    @SerializedName("site")
    public String mSiteUrl;

    @SerializedName("groups")
    public List<Integer> mGroups;

    @SerializedName("loggedIn")
    public boolean isLoggedIn;

    @SerializedName("administerUser")
    public boolean isAdministerUser;

    public boolean equals(int id) {
        return String.valueOf(id).equals(this.id);
    }
}
