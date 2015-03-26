package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class Message {

    @SerializedName("id")
    protected int id;

    @SerializedName("site")
    public String mSiteUrl;

    @SerializedName("authorId")
    public int mAuthorId;

    @SerializedName("groupId")
    public int mGroupId;

    @SerializedName("changed")
    protected Date mLastChanged;

    @SerializedName("lastCommentAuthorId")
    protected int mLastCommentAuthorId;

    @SerializedName("lastCommentDate")
    protected Date mLastCommentDate;

    @SerializedName("commentCount")
    public int mCommentCount;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("lastActivity")
    public Date mLastActivity;

    @SerializedName("messageLine")
    public String mMessageLine;

    @SerializedName("read")
    public boolean hasBeenRead;
}
