package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by steffenkarlsson on 20/03/15.
 */

@Parcel
public class User {

    public transient AccessToken mAccessToken;

    @SerializedName("name")
    public String mName;

    @SerializedName("picture")
    public String mPictureUrl;

    @SerializedName("sites")
    public List<Site> mSites;


    public Site getSiteById(String id) {
        if (mSites != null) {
            for (Site site : mSites) {
                if (site.equals(id))
                    return site;
            }
        }
        return null;
    }

    public boolean isSingleUser() {
        return mSites == null || mSites.size() < 2;
    }

    public Site getSiteByUrl(String url){
        if (mSites != null) {
            for (Site site : mSites) {
                if (site.mSiteUrl.equals(url))
                    return site;
            }
        }
        return null;
    }
}
