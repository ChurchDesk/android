package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by root on 6/1/15.
 */

@Parcel
public class Holyday extends BaseDay {

    @Override
    public Date getDate() {
        return mDate;
    }

    @SerializedName("date")
    protected Date mDate;

    @SerializedName("name")
    public String mName;
}
