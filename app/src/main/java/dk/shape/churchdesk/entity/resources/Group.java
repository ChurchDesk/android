package dk.shape.churchdesk.entity.resources;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class Group {

    @SerializedName("id")
    protected String id;

    @SerializedName("name")
    public String mName;

    @SerializedName("site")
    protected String mSiteUrl;
}
