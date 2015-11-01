package dk.shape.churchdesk.entity.resources;

import com.google.gson.annotations.SerializedName;
import org.parceler.Parcel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class Category extends Taxonomy {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String mName;

    @SerializedName("color")
    protected String mColor;

    @SerializedName("organizationId")
    public String mSiteUrl;

    @SerializedName("isMaster")
    public boolean mIsMaster;

    public int getColor() {
        return getColor(Integer.valueOf(mColor));
    }

    public int getId(){
        return Integer.valueOf(id);
    }
}
