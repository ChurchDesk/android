package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class CommentObj {

    @SerializedName("id")
    public int id;

    @SerializedName("organizationId")
    public String mSiteUrl;

    @SerializedName("authorId")
    public int mAuthorId;

    @SerializedName("groupId")
    public int mGroupId;

    @SerializedName("updatedAt")
    public Date mChanged;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("message")
    public String mBody;

    @SerializedName("replies")
    public List<Comment> mComments;
}
