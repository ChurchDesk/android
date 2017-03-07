package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chirag on 24/01/2017.
 */
public class Segment {
    @SerializedName("name")
    public String mName;

    @SerializedName("id")
    public int mSegmentId;
}
