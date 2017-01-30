package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.apache.commons.collections.list.TreeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by steffenkarlsson on 20/03/15.
 */

@Parcel
public class User {

    public transient AccessToken mAccessToken;

    @SerializedName("id")
    public String mUserId;
    @SerializedName("email")
    public String mEmail;

    @SerializedName("fullName")
    public String mName;

    @SerializedName("picture")
    public HashMap<String, String> mPictureUrl;

    @SerializedName("locale")
    public HashMap<String, String> mLocale;

    @SerializedName("organizations")
    public List<Site> mSites;

    public PushNotification mNotification;

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

    public PushNotification getNotifications() {
        return mNotification;
    }

    public void setNotifications(PushNotification notification) {
        this.mNotification = notification;
    }



}
