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

    @SerializedName("organizationId")
    public String mSiteUrl;

    @SerializedName("authorId")
    public int mAuthorId;

    @SerializedName("groupId")
    public int mGroupId;

    @SerializedName("updatedAt")
    protected Date mLastChanged;

    @SerializedName("lastReplyName")
    public String mLastCommentAuthorName;

    //@SerializedName("lastReplyTime")
    //protected Date mLastCommentDate;

    @SerializedName("commentCount")
    public int mCommentCount;

    @SerializedName("title")
    public String mTitle;

    @SerializedName("lastReplyTime")
    public Date mLastActivity;

    @SerializedName("lastMessageLine")
    public String mMessageLine;

    @SerializedName("hasRead")
    public boolean hasBeenRead;


    @Override
    public boolean equals(Object o) {
        Message oMessage = (Message)o;
        Boolean sameTitle = false;
        if (oMessage.mTitle != null){
            sameTitle = mTitle.equals(oMessage.mTitle);
        }
        Boolean sameMessage = false;
        if (oMessage.mMessageLine != null){
            sameMessage = mMessageLine.equals(oMessage.mMessageLine);
        }
        return mSiteUrl.equals(oMessage.mSiteUrl)
                && mGroupId == oMessage.mGroupId
                && id == oMessage.id
                && sameTitle
                && sameMessage;
    }
}
