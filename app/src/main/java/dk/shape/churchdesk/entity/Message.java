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
    public int id;

    @SerializedName("site")
    public String mSiteUrl;

    @SerializedName("authorId")
    public int mAuthorId;

    @SerializedName("groupId")
    public int mGroupId;

    @SerializedName("changed")
    protected Date mLastChanged;

    @SerializedName("lastCommentAuthorId")
    public int mLastCommentAuthorId;

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


    @Override
    public boolean equals(Object o) {
        Message oMessage = (Message)o;
        return mSiteUrl.equals(oMessage.mSiteUrl)
                && mGroupId == oMessage.mGroupId
                && id == oMessage.id
                && mTitle.equals(oMessage.mTitle)
                && mMessageLine.equals(oMessage.mMessageLine);
    }
}
