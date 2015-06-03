package dk.shape.churchdesk.entity.resources;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class Group {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String mName;

    @SerializedName("site")
    public String mSiteUrl;

    public boolean equals(int id) {
        return String.valueOf(id).equals(this.id);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Group && ((Group) o).id.equals(this.id);
    }

    public int getId(){
        return Integer.valueOf(id);
    }
}
