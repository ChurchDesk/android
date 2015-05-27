package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class CommentObj {

    @SerializedName("comments")
    public List<Comment> mComments;
}
