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

    @SerializedName("site")
    public String mSiteUrl;

    @SerializedName("authorId")
    public int mAuthorId;

    @SerializedName("groupId")
    public int mGroupId;

    @SerializedName("changed")
    public Date mChanged;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("body")
    public String mBody;

    @SerializedName("comments")
    public List<Comment> mComments;
}
