package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chirag on 23/02/2017.
 */
public class Tag {
    @SerializedName("id")
    public Integer mTagId;

    @SerializedName("name")
    public String mTagName;
}
