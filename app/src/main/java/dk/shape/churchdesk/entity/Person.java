package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chirag on 24/01/2017.
 */
@Parcel
public class Person {
    @SerializedName("fullName")
    public String mFullName;

    @SerializedName("firstName")
    public String mFirstName;

    @SerializedName("lastName")
    public String mLastName;

    @SerializedName("occupation")
    public String mOccupation;

    @SerializedName("email")
    public String mEmail;

    @SerializedName("id")
    public int mPeopleId;

    @SerializedName("gender")
    public String mGender;

    @SerializedName("birthday")
    public Date mBirthday;

    @SerializedName("registered")
    public Date mRegistered;

    @SerializedName("contact")
    public HashMap<String, String> mContact;

    @SerializedName("tags")
    public List<Tag> mTags;

}
