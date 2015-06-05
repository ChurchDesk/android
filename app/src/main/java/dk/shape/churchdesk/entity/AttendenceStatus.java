package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Martin on 04/06/2015.
 */
@Parcel
public class AttendenceStatus {

    @SerializedName("user")
    public String mUser;

    @SerializedName("status")
    public String mStatus;

    public int getStatus(){
        return Integer.valueOf(mStatus);
    }

    public int getUser(){
        return Integer.valueOf(mUser);
    }


}
