package dk.shape.churchdesk.entity.resources;

import android.graphics.Color;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class Resource extends Taxonomy {

    @SerializedName("id")
    protected String id;

    @SerializedName("name")
    public String mName;

    @SerializedName("color")
    protected String mColor;

    @SerializedName("organizationId")
    public String mSiteUrl;

    public int getColor() {
        return getColor(Integer.valueOf(mColor));
    }

    public int getId(){
        return Integer.valueOf(id);
    }
}
