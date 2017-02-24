package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by chirag on 23/02/2017.
 */
@Parcel
public class Tag {
    @SerializedName("id")
    public Integer mTagId;

    @SerializedName("name")
    public String mTagName;
}
