package dk.shape.churchdesk.entity.resources;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

import dk.shape.churchdesk.entity.Site;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class OtherUser {

    @SerializedName("id")
    public Integer id;

    @SerializedName("name")
    public String mName;

    @SerializedName("status")
    public String mStatus;

    @SerializedName("picture")
    public String mPictureUrl;

    @SerializedName("roles")
    public List<Integer> mRoles;

    @SerializedName("organizationId")
    public String mSiteUrl;

    @SerializedName("organizations")
    public List<Site> mOrganizations;

    @SerializedName("groups")
    public List<Integer> mGroups;

    @SerializedName("loggedIn")
    public boolean isLoggedIn;

    @SerializedName("administerUser")
    public boolean isAdministerUser;

    @SerializedName("email")
    public String sEmail;

    @SerializedName("image")
    public String sImage;

    @SerializedName("attending")
    public String sAttending;

    public boolean equals(int id) {
        return String.valueOf(id).equals(this.id);
    }

    public int getId(){
        return Integer.valueOf(id);
    }

    public boolean isMemberOfOrganization(Integer organizationId) {
        for (Site organization : this.mOrganizations) {
            if (organization.iOrganizationId.equals(organizationId)) {
                return true;
            }
        }
        return false;
    }
}
