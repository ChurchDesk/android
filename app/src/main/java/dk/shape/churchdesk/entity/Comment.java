package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class Comment {

    @SerializedName("id")
    public int id;

    @SerializedName("site")
    public String mSiteUrl;

    @SerializedName("authorId")
    public int mAuthorId;

    @SerializedName("authorName")
    public String mAuthorName;

    @SerializedName("created")
    public Date mCreated;

    @SerializedName("targetId")
    public int mTargetId;

    @SerializedName("body")
    public String mBody;

    @SerializedName("read")
    public boolean read;

    @SerializedName("canEdit")
    public boolean canEdit;

    @SerializedName("canDelete")
    public boolean canDelete;
}
